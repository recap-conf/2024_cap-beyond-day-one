package my.bookshop.handlers;

import com.sap.cds.services.EventContext;
import com.sap.cds.services.cds.ApplicationService;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.ServiceName;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Component;
@Component
@ServiceName(value = "*", type = ApplicationService.class)
public class TraceHandler implements EventHandler {

    private OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();

    //@Before(event={CqnService.EVENT_READ, CqnService.EVENT_UPDATE, CqnService.EVENT_UPSERT, CqnService.EVENT_CREATE, CqnService.EVENT_DELETE}, entity="*")
    //@HandlerOrder(HandlerOrder.EARLY)
    public void beforeRead(EventContext ctx) {
        System.out.println("Starting span");
        Tracer tracer = openTelemetry.getTracer("TraceHandler.execution.tracer");
        Span span = tracer.spanBuilder(ctx.getService().getName() + "." + ctx.getEvent()).startSpan();
        if(ctx instanceof CdsReadEventContext) {
            CdsReadEventContext readCtx = (CdsReadEventContext) ctx;
            span.setAttribute("query", readCtx.getCqn().toString());
        }

        span.makeCurrent();
    }

    //@After(event={CqnService.EVENT_READ, CqnService.EVENT_UPDATE, CqnService.EVENT_UPSERT, CqnService.EVENT_CREATE, CqnService.EVENT_DELETE}, entity="*")
    //@HandlerOrder(HandlerOrder.EARLY)
    public void post() {
        System.out.println("Ending span");
        Span span = Span.current();
        span.end();
    }
}
