package net.atos.ari.health.khealthinair.cdr.api.repository;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import net.atos.ari.health.khealthinair.cdr.api.config.FhirConfigProperties;

@Service
@Scope("singleton")
@Order(0)
public class RepositoryConnector {

  private static final Logger logger = LoggerFactory.getLogger(RepositoryConnector.class);

  private FhirContext ctx;
  private IGenericClient client;

  /**
   * Creates context and client for repository connections
   *
   * Trying fields for repository connector objects In case this is not optimal for concurrency,
   * find another strategy
   *
   * @param endpoint is injected from properties
   */
  @Autowired
  public RepositoryConnector(FhirConfigProperties fhirConfigProperties) {
    ctx = FhirContext.forR5(); // Expensive
    ctx.getRestfulClientFactory().setSocketTimeout(200 * 1000);
    // Header header = new BasicHeader("x-api-key", fhirConfigProperties.getAPIKey());
    // ArrayList<Header> headers = new ArrayList<Header>();
    // headers.add(header);
    // HttpClientBuilder builder = HttpClientBuilder.create();
    // builder.setDefaultHeaders(headers);
    // CloseableHttpClient httpClient = builder.build();
    // ctx.getRestfulClientFactory().setHttpClient(httpClient);
    logger.info("HDH-CDR endpoint: {}", fhirConfigProperties.getEndpoint());
    client = ctx.newRestfulGenericClient(fhirConfigProperties.getEndpoint()); // Cheap
  }

  /**
   * Generic method for creating a resource.
   *
   * @param resource resource
   * @return id for new resource
   */
  public IIdType createResourceReturnId(IBaseResource resource) {
    IIdType id = client.create().resource(resource).cacheControl(CacheControlDirective.noCache())
        .execute().getId();
    // update Resource with final assigned ID so that subsequent References are built correctly
    resource.setId(id);
    // logger.trace("Resource [{}] created with id [{}]", resource.getClass().toGenericString(),
    // id);
    return id;
  }

  /**
   * Generic method for reading a resource.
   *
   * @param id identifier
   * @param domainResource type of resource
   * @return
   */
  public <T extends IBaseResource> T readResource(IIdType id, Class<T> type) {
    // logger.trace("Reading resource [{}] with id [{}]", type.getClass().toGenericString(),
    // id.getIdPart());
    return client.read().resource(type).withId(id).cacheControl(CacheControlDirective.noCache())
        .execute();
  }

  /**
   * Creates the resource.
   *
   * @param resource resource
   * @return result
   */
  public MethodOutcome createResource(IBaseResource resource) {
    // logger.trace("Creating resource [{}] with id [{}]", resource.getClass().toGenericString(),
    // resource.getIdElement());
    return client.create().resource(resource).cacheControl(CacheControlDirective.noCache())
        .execute();
  }

  /**
   * Updates the resource.
   *
   * @param resource resource
   * @return result
   */
  public MethodOutcome updateResource(IBaseResource resource) {
    // logger.trace("Updating resource [{}] with id [{}]", resource.getClass().toGenericString(),
    // resource.getIdElement());
    return client.update().resource(resource).cacheControl(CacheControlDirective.noCache())
        .execute();
  }

  /**
   * Deletes the resource.
   *
   * @param resource resource
   * @return result
   */
  public MethodOutcome deleteResource(IBaseResource resource) {
    // logger.trace("Deleting resource [{}] with id [{}]", resource.getClass().toGenericString(),
    // resource.getIdElement());
    return client.delete().resource(resource).cacheControl(CacheControlDirective.noCache())
        .execute();
  }

  /**
   * Encodes the resource to string.
   *
   * @param resource resource
   * @return string
   */
  public String encodeResourceToString(IBaseResource resource) {
    return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resource);
  }

  /**
   * Executes the transaction described in bundle.
   *
   * @param bundle bundle
   * @return bundle
   */
  public Bundle executeTransactionWithBundle(Bundle bundle) {
    // logger.trace("Creating bundle in repository: {}",
    // ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle));
    Bundle resp = client.transaction().withBundle(bundle)
        .cacheControl(CacheControlDirective.noCache()).execute();
    return resp;
  }

  /**
   * Provides the client for native operations like resource search
   *
   * @return
   */
  public IGenericClient client() {
    return client;
  }

  /**
   * Provides the ctx for native operations
   *
   * @return
   */
  public FhirContext ctx() {
    return ctx;
  }

}

