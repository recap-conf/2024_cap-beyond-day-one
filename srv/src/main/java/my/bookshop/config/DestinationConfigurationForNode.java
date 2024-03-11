package my.bookshop.config;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultDestinationLoader;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Profile("cap-node")
public class DestinationConfigurationForNode {

	@Autowired
	private Environment environment;

	@EventListener
	void applicationReady(ApplicationReadyEvent ready) {
		String url = environment.getProperty("bupa.node.url", String.class);
		String destinationName = environment.getProperty("cds.remote.services.'[API_BUSINESS_PARTNER]'.destination.name");
		if(url != null && destinationName != null) {
			DefaultHttpDestination httpDestination = DefaultHttpDestination
			.builder(url)
			.basicCredentials(new BasicCredentials("admin", "admin"))
			.name(destinationName).build();

			DestinationAccessor.prependDestinationLoader(
				new DefaultDestinationLoader().registerDestination(httpDestination));
		}

	}

}
