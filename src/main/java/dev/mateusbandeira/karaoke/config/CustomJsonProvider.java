package dev.mateusbandeira.karaoke.config;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class CustomJsonProvider extends JacksonJaxbJsonProvider {

	public static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		// Exclude fields without @jsonview annotation
		mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public CustomJsonProvider() {
		super();
		setMapper(mapper);
	}
	
	public static ObjectWriter writerWithView(Class<?> serializationView) {
		return mapper.writerWithView(serializationView);
	}
}