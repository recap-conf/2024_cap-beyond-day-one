package my.bookshop;

import cds.gen.adminservice.Books_;
import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.runtime.CdsRuntime;
import com.sap.cds.services.runtime.RequestContextRunner;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
public class TestController {

    @Autowired
    CdsRuntime cdsRuntime;

    @Autowired
    private ObservationRegistry observationRegistry;
    @Autowired
    private MeterRegistry meterRegistry;

    private OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();

    @GetMapping(path = "/sayhello/metrics")
    public ResponseEntity<Void> metrics() {
        //explicit metric using micrometer
        meterRegistry.counter("sayhelloCounter.micrometer").increment();

        //explicit metric using open telemetry api
        openTelemetry.getMeter("TestController.execution.meter").counterBuilder("sayhelloCounter.otel").build().add(1);

        return ResponseEntity.ok().build();
    }
    @GetMapping(path = "/sayhello/traces")
    public ResponseEntity<String> traces() {
        if(observationRegistry==null) {
            return ResponseEntity.internalServerError().body("Registry is null");
        }
        StringBuilder builder = new StringBuilder();

        //direct usage of open telemetry api for tracing
        Tracer tracer = openTelemetry.getTracer("TestController.execution.tracer");
        Span span = tracer.spanBuilder("sayhelloOtel").startSpan();//automatically detects potential parent span in the same thread
        try(Scope scope = span.makeCurrent()) {


            System.out.println("span id: " + Span.current().getSpanContext().getSpanId());
            System.out.println("trace id: " + Span.current().getSpanContext().getTraceId());

            Observation.createNotStarted("nested_observation", observationRegistry).observe(() -> {
                System.out.println("Hello2");
                System.out.println("span id: " + Span.current().getSpanContext().getSpanId());
                System.out.println("trace id: " + Span.current().getSpanContext().getTraceId());
            });

        } finally {
            span.end();
        }

        //use Obervation api
        Observation.createNotStarted("sayhelloObservation", observationRegistry).observe(() -> {
            System.out.println("Hello");
            System.out.println("span id: " + Span.current().getSpanContext().getSpanId());
            System.out.println("trace id: " + Span.current().getSpanContext().getTraceId());

            Map<String, String> env = System.getenv();

            for (Map.Entry<String, String> e : env.entrySet()) {
                builder.append(e.getKey() + ": " + e.getValue() + "\n");
            }

            String javaOpts = System.getenv("JAVA_OPTS");
            builder.append("JAVA_OPTS:  " + javaOpts);
        });



        return ResponseEntity.ok().body(builder.toString());
    }

    @GetMapping(path = "/thread_propagation")
    public ResponseEntity<String> threadPropagation(@RequestParam(required = false)boolean synch) throws Exception {
        RequestContextRunner runner = cdsRuntime.requestContext();
        Future<Result> result = Executors.newSingleThreadExecutor().submit(() -> {
            return runner.run(threadContext -> {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                PersistenceService persistenceService = threadContext.getServiceCatalog().getServices(PersistenceService.class).findFirst().get();
                return persistenceService.run(Select.from(Books_.class));
            });
        });

        if(synch) {
            return ResponseEntity.ok("Result count " + result.get().rowCount());
        } else {
            return ResponseEntity.ok("Asynchronous");
        }
    }

    @GetMapping(path = "/thread_propagation2")
    public ResponseEntity<String> threadPropagation2(@RequestParam(required = false)boolean synch) throws Exception {
        RequestContextRunner runner = cdsRuntime.requestContext();

        final Span outerSpan = Span.current();
        Context outerContext = Context.current();
        Thread t = new Thread() {
            public void run() {
                runner.run(threadContext -> {
                    //Span.fromContextOrNull(outerContext);
                    Tracer tracer = openTelemetry.getTracer("TestController.execution.tracer");
                    Span span = tracer.spanBuilder("df")/*.setParent(outerContext)*/.startSpan();
                    try {
                        Thread.sleep(1000); //to ensure that the asynchronicity is visible in the diagrams
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    PersistenceService persistenceService = cdsRuntime.getServiceCatalog().getServices(PersistenceService.class).findFirst()
                            .get();
                    persistenceService.run(Select.from(Books_.class));

                    span.end();
                });
            }
        };
        t.start();

        return ResponseEntity.ok("done");
    }

     /*cdsRuntime.requestContext().run((ctx) -> {
            AuditLogService auditLog = ctx.getServiceCatalog().getServices(AuditLogService.class).findFirst().get();
            Access access = Access.create();
            KeyValuePair pair = KeyValuePair.create();
            pair.setKeyName("key1");
            pair.setValue("value1");
            DataSubject subject = DataSubject.create();
            subject.setType("a");
            subject.setId(Collections.singletonList(pair));
            access.setDataSubject(subject);
            DataObject dObject = DataObject.create();
            dObject.setType("b");
            dObject.setId(Collections.singletonList(pair));
            access.setDataObject(dObject);
            Attribute attr = Attribute.create();
            attr.setName("attr1");
            access.setAttributes(Collections.singletonList(attr));
            auditLog.logDataAccess(access);
        });*/
}
