package rocks.inspectit.server.open.xtrace;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.diagnoseit.spike.inspectit.trace.impl.IITUseCaseInvocation;
import org.spec.research.open.xtrace.api.core.AdditionalInformation;
import org.spec.research.open.xtrace.api.core.SubTrace;
import org.spec.research.open.xtrace.api.core.Trace;
import org.spec.research.open.xtrace.api.core.UseCaseInvocation;
import org.spec.research.open.xtrace.api.core.callables.HTTPRequestProcessing;
import org.spec.research.open.xtrace.api.core.callables.MethodInvocation;
import org.spec.research.open.xtrace.api.core.callables.RemoteInvocation;

/**
 * OPEN.xtrace serializer.
 * 
 * @author Tobias Angerstein
 *
 */
public class OPENxtraceSerializer {

	ObjectMapper objectMapper;

	public OPENxtraceSerializer() {
		createObjectMapper();
	}

	/**
	 * Serializes OPEN.xtrace into JSON.
	 * 
	 * @param traceImplList
	 *            list of traces
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @return the JSON string
	 */
	public String serialize(List<Trace> traceImplList) {
		try {
			return objectMapper.writeValueAsString(traceImplList);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates Object Mapper and registers Serializers
	 */
	private void createObjectMapper() {
		objectMapper = new ObjectMapper();

		SimpleModule serializationModule = new SimpleModule("", new Version(2, 3, 2, "Serialization open.XTRACE"));
		serializationModule.addSerializer(IITUseCaseInvocation.class, new IITUseCaseSerializer());
		serializationModule.addSerializer(Trace.class, new TraceSerializer());
		serializationModule.addSerializer(SubTrace.class, new SubTraceSerializer());
		serializationModule.addSerializer(RemoteInvocation.class, new RemoteInvocationSerializer());
		serializationModule.addSerializer(HTTPRequestProcessing.class, new HTTPRequestProcessingSerializer());
		serializationModule.addSerializer(MethodInvocation.class, new MethodInvocationSerializer());

		objectMapper.registerModule(serializationModule);
	}

	// Serializers

	/**
	 * Serializes {@link UseCaseInvocation}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	class IITUseCaseSerializer extends JsonSerializer<UseCaseInvocation> {

		public IITUseCaseSerializer() {
			super();
		}

		@Override
		public void serialize(UseCaseInvocation value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
			jgen.writeStartObject();
			jgen.writeNumberField("timeStamp", value.getTimestamp());
			jgen.writeObjectField("children", value.getCallees());

			if (value.getAdditionalInformation().isPresent()) {
				for (AdditionalInformation additionalInformation : value.getAdditionalInformation().get()) {
					jgen.writeObjectField("additionalInformation" + additionalInformation.getName(), additionalInformation.getValue());
				}
			}
			jgen.writeNumberField("exclusiveTime", value.getExclusiveTime());
			jgen.writeNumberField("responseTime", value.getResponseTime());
			jgen.writeNumberField("exitTime", value.getExitTime());
			jgen.writeNumberField("threadID", value.getThreadID().orElse(-1l));
			jgen.writeStringField("threadName", value.getThreadName().orElse("undefined"));
			jgen.writeObjectField("identifier", value.getIdentifier().orElse(null));
			jgen.writeObjectField("labels", value.getLabels().orElse(null));

			jgen.writeStringField("@class", UseCaseInvocation.class.getCanonicalName());
			jgen.writeEndObject();
		}
	}

	/**
	 * Serializes {@link Trace}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	class TraceSerializer extends JsonSerializer<Trace> {

		public TraceSerializer() {
			super();
		}

		@Override
		public void serialize(Trace value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
			jgen.writeStartObject();
			jgen.writeObjectField("rootOfTrace", value.getRoot());
			jgen.writeNumberField("traceId", value.getTraceId());
			jgen.writeNumberField("exclusiveTime", value.getExclusiveTime());
			jgen.writeNumberField("responseTime", value.getResponseTime());
			jgen.writeObjectField("identifier", value.getIdentifier().orElse(null));
			jgen.writeStringField("@class", Trace.class.getCanonicalName());
			jgen.writeEndObject();
		}
	}

	/**
	 * Serializes {@link SubTrace}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	class SubTraceSerializer extends JsonSerializer<SubTrace> {

		public SubTraceSerializer() {
			super();
		}

		@Override
		public void serialize(SubTrace value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
			jgen.writeStartObject();
			jgen.writeObjectField("rootOfSubTrace", value.getRoot());
			jgen.writeObjectField("identifier", value.getIdentifier().orElse(null));
			jgen.writeNumberField("subTraceId", value.getSubTraceId());
			jgen.writeObjectField("subTraces", value.getSubTraces());
			jgen.writeNumberField("exclusiveTime", value.getExclusiveTime());
			jgen.writeNumberField("responseTime", value.getResponseTime());

			jgen.writeStringField("application", value.getLocation().getApplication().orElse("undefined"));
			jgen.writeStringField("businessTransaction", value.getLocation().getBusinessTransaction().orElse("undefined"));
			jgen.writeStringField("host", value.getLocation().getHost());
			jgen.writeStringField("serverName", value.getLocation().getServerName().orElse("undefined"));
			jgen.writeStringField("runtimeEnvironment", value.getLocation().getRuntimeEnvironment().orElse("undefined"));
			jgen.writeStringField("nodeType", value.getLocation().getNodeType().orElse("undefined"));

			jgen.writeStringField("@class", SubTrace.class.getCanonicalName());
			jgen.writeEndObject();
		}
	}

	/**
	 * Serializes {@link RemoteInvocation}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	class RemoteInvocationSerializer extends JsonSerializer<RemoteInvocation> {

		public RemoteInvocationSerializer() {
			super();
		}

		@Override
		public void serialize(RemoteInvocation value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
			jgen.writeStartObject();
			jgen.writeNumberField("timeStamp", value.getTimestamp());

			if (value.getAdditionalInformation().isPresent()) {
				for (AdditionalInformation additionalInformation : value.getAdditionalInformation().get()) {
					jgen.writeObjectField("additionalInformation" + additionalInformation.getName(), additionalInformation.getValue());
				}
			}

			jgen.writeNumberField("exclusiveTime", value.getExclusiveTime());
			jgen.writeNumberField("responseTime", value.getResponseTime());
			jgen.writeNumberField("exitTime", value.getExitTime());
			jgen.writeNumberField("threadID", value.getThreadID().orElse(-1l));
			jgen.writeStringField("threadName", value.getThreadName().orElse("undefined"));
			jgen.writeObjectField("identifier", value.getIdentifier().orElse(null));
			jgen.writeStringField("target", value.getTarget());
			jgen.writeObjectField("labels", value.getLabels().orElse(null));
			jgen.writeStringField("@class", RemoteInvocation.class.getCanonicalName());

			jgen.writeObjectField("targetSubTrace", value.getTargetSubTrace().orElse(null));

			jgen.writeEndObject();
		}
	}

	/**
	 * Serializes {@link HTTPRequestProcessing}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	class HTTPRequestProcessingSerializer extends JsonSerializer<HTTPRequestProcessing> {

		public HTTPRequestProcessingSerializer() {
			super();
		}

		@Override
		public void serialize(HTTPRequestProcessing value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
			jgen.writeStartObject();
			jgen.writeNumberField("timeStamp", value.getTimestamp());

			if (value.getAdditionalInformation().isPresent()) {
				for (AdditionalInformation additionalInformation : value.getAdditionalInformation().get()) {
					jgen.writeObjectField("additionalInformation" + additionalInformation.getName(), additionalInformation.getValue());
				}
			}
			jgen.writeNumberField("exclusiveTime", value.getExclusiveTime());
			jgen.writeNumberField("responseTime", value.getResponseTime());
			jgen.writeNumberField("exitTime", value.getExitTime());
			jgen.writeNumberField("threadID", value.getThreadID().orElse(-1l));
			jgen.writeStringField("threadName", value.getThreadName().orElse("undefined"));
			jgen.writeObjectField("identifier", value.getIdentifier().orElse(null));
			jgen.writeObjectField("labels", value.getLabels().orElse(null));
			jgen.writeObjectField("children", value.getCallees());

			jgen.writeStringField("uri", value.getUri());
			jgen.writeObjectField("responseHTTPHeaders", value.getResponseHTTPHeaders().orElse(null));
			jgen.writeNumberField("responseCode", value.getResponseCode().orElse(-1l));
			jgen.writeObjectField("requestMethod", value.getRequestMethod().orElse(null));
			jgen.writeObjectField("HTTPSessionAttributes", value.getHTTPSessionAttributes().orElse(null));
			jgen.writeObjectField("HTTPParameters", value.getHTTPParameters().orElse(null));
			jgen.writeObjectField("HTTPHeaders", value.getHTTPHeaders().orElse(null));
			jgen.writeObjectField("HTTPAttributes", value.getHTTPAttributes().orElse(null));

			jgen.writeStringField("@class", HTTPRequestProcessing.class.getCanonicalName());
			jgen.writeEndObject();
		}
	}

	/**
	 * Serializes {@link MethodInvocation}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	class MethodInvocationSerializer extends JsonSerializer<MethodInvocation> {

		public MethodInvocationSerializer() {
			super();
		}

		@Override
		public void serialize(MethodInvocation value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
			jgen.writeStartObject();
			jgen.writeNumberField("timeStamp", value.getTimestamp());

			if (value.getAdditionalInformation().isPresent()) {
				for (AdditionalInformation additionalInformation : value.getAdditionalInformation().get()) {
					jgen.writeObjectField("additionalInformation" + additionalInformation.getName(), additionalInformation.getValue());
				}
			}
			jgen.writeNumberField("exclusiveTime", value.getExclusiveTime());
			jgen.writeNumberField("responseTime", value.getResponseTime());
			jgen.writeNumberField("exitTime", value.getExitTime());
			jgen.writeNumberField("threadID", value.getThreadID().orElse(-1l));
			jgen.writeStringField("threadName", value.getThreadName().orElse("undefined"));
			jgen.writeObjectField("identifier", value.getIdentifier().orElse(null));
			jgen.writeObjectField("labels", value.getLabels().orElse(null));
			jgen.writeObjectField("children", value.getCallees());

			jgen.writeNumberField("syncTime", value.getSyncTime().orElse(-1l));
			jgen.writeStringField("signature", value.getSignature());
			jgen.writeObjectField("returnValue", value.getReturnValue().orElse(null));
			jgen.writeStringField("returnType", value.getReturnType().orElse("undefined"));
			jgen.writeObjectField("parameterValues", value.getParameterValues().orElse(null));
			jgen.writeObjectField("parameterTypes", value.getParameterTypes().orElse(null));
			jgen.writeStringField("packageName", value.getPackageName().orElse("undefined"));
			jgen.writeStringField("methodName", value.getMethodName().orElse("undefined"));
			jgen.writeNumberField("GCTime", value.getGCTime().orElse(-1l));
			jgen.writeNumberField("exclusiveWaitTime", value.getExclusiveWaitTime().orElse(-1l));
			jgen.writeNumberField("exclusiveSyncTime", value.getExclusiveSyncTime().orElse(-1l));
			jgen.writeNumberField("exclusiveGCTime", value.getExclusiveGCTime().orElse(-1l));
			jgen.writeNumberField("exclusiveCPUTime", value.getExclusiveCPUTime().orElse(-1l));
			jgen.writeNumberField("CPUTime", value.getCPUTime().orElse(-1l));
			jgen.writeStringField("className", value.getClassName().orElse("undefined"));

			jgen.writeStringField("@class", MethodInvocation.class.getCanonicalName());
			jgen.writeEndObject();
		}
	}
}
