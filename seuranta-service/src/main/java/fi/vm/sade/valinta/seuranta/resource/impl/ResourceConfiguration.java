package fi.vm.sade.valinta.seuranta.resource.impl;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class ResourceConfiguration extends ResourceConfig {
    public ResourceConfiguration() {
        // packages("fi.vm.sade.valinta.dokumenttipalvelu.resource.impl.DokumenttiResourceImpl");
        register(new ContainerResponseFilter() {
            @Override
            public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
                responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
            }
        });
        register(SseFeature.class);
        register(JacksonFeature.class);
        register(DokumentinSeurantaResourceImpl.class);
        register(LaskennanSeurantaResourceImpl.class);
        register(SijoittelunSeurantaResourceImpl.class);
        register(SessionResourceImpl.class);
        registerInstances(
                new io.swagger.jaxrs.listing.SwaggerSerializers(),
                new io.swagger.jaxrs.listing.ApiListingResource(),
                new io.swagger.jaxrs.listing.AcceptHeaderApiListingResource());
    }
}
