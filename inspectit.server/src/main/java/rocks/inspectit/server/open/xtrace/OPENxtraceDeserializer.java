package rocks.inspectit.server.open.xtrace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.type.TypeReference;
import org.diagnoseit.spike.inspectit.trace.impl.IITSubTraceImpl;
import org.diagnoseit.spike.inspectit.trace.impl.IITTraceImpl;
import org.spec.research.open.xtrace.api.core.AdditionalInformation;
import org.spec.research.open.xtrace.api.core.SubTrace;
import org.spec.research.open.xtrace.api.core.Trace;
import org.spec.research.open.xtrace.api.core.UseCaseInvocation;
import org.spec.research.open.xtrace.api.core.callables.Callable;
import org.spec.research.open.xtrace.api.core.callables.HTTPMethod;
import org.spec.research.open.xtrace.api.core.callables.HTTPRequestProcessing;
import org.spec.research.open.xtrace.api.core.callables.MethodInvocation;
import org.spec.research.open.xtrace.api.core.callables.RemoteInvocation;
import org.spec.research.open.xtrace.dflt.impl.core.LocationImpl;
import org.spec.research.open.xtrace.dflt.impl.core.SubTraceImpl;
import org.spec.research.open.xtrace.dflt.impl.core.TraceImpl;
import org.spec.research.open.xtrace.dflt.impl.core.callables.AbstractCallableImpl;
import org.spec.research.open.xtrace.dflt.impl.core.callables.HTTPRequestProcessingImpl;
import org.spec.research.open.xtrace.dflt.impl.core.callables.MethodInvocationImpl;
import org.spec.research.open.xtrace.dflt.impl.core.callables.RemoteInvocationImpl;
import org.spec.research.open.xtrace.dflt.impl.core.callables.UseCaseInvocationImpl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * OPEN.xtrace deserializer.
 * 
 * @author Tobias Angerstein
 *
 */
public class OPENxtraceDeserializer {

	ObjectMapper objectMapper;

	TypeReference<ArrayList<String>> listTypRef = new TypeReference<ArrayList<String>>() {
	};
	TypeReference<HashMap<String, String>> mapTypRef = new TypeReference<HashMap<String, String>>() {
	};
	public OPENxtraceDeserializer() {
		createObjectMapper();
	}

	// /**
	// * Sets missing cyclical references, which could not be serialized.
	// *
	// * @param traceImpl
	// * trace.
	// * @return trace including cyclical references.
	// */
	// private TraceImpl setMissingReferences(TraceImpl traceImpl) {
	// traceImpl.getRoot().getContainingTrace();
	// IITSubTraceImpl subTraceImpl = (SubTraceImpl) traceImpl.getRoot();
	// setMissingReferencesInCallable((AbstractCallable) subTraceImpl.getRoot(), null,
	// subTraceImpl);
	// return traceImpl;
	// }
	//
	// /**
	// * Set missing references in callable.
	// *
	// * @param callable
	// * current callable
	// * @param parent
	// * parent callable
	// * @param subTraceImpl
	// * containing sub trace
	// */
	// private void setMissingReferencesInCallable(Callable callable, AbstractNestingCallableImpl
	// parent, SubTraceImpl subTraceImpl) {
	// if (callable instanceof IITAbstractCallable) {
	// ((IITAbstractCallable) callable).setContainingSubTrace((SubTrace) subTraceImpl);
	// ((IITAbstractCallable) callable).setParent(parent);
	// }
	// if (callable instanceof IITAbstractNestingCallable) {
	// for (Callable childCallable : ((IITAbstractNestingCallable) callable).getCallees()) {
	// setMissingReferencesInCallable(childCallable, ((IITAbstractNestingCallable) callable),
	// subTraceImpl);
	// }
	// }
	// }

	/**
	 * Deserializes JSON into a list of {@link IITTraceImpl}.
	 * 
	 * @param serializedTrace
	 *            JSON string
	 * @return a @{@link ArrayList} containing {@link IITTraceImpl}}
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public List<IITTraceImpl> deserialize(String serializedTrace) throws JsonGenerationException, JsonMappingException, IOException {
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		JsonElement tradeElement = parser.parse(serializedTrace);
		JsonArray jsonTraceArray = tradeElement.getAsJsonArray();

		ArrayList<IITTraceImpl> resultList = new ArrayList<IITTraceImpl>();

		// for (JsonElement element : jsonTraceArray) {
		// resultList.add(setMissingReferences(objectMapper.readValue(element.toString(),
		// IITTraceImpl.class)));
		// }

		return resultList;
	}

	/**
	 * Creates Object Mapper and registers Serializers.
	 */
	private void createObjectMapper() {
		objectMapper = new ObjectMapper();

		SimpleModule deserializationModule = new SimpleModule("", new Version(2, 3, 2, "Deserialization open.XTRACE"));
		deserializationModule.addDeserializer(UseCaseInvocation.class, new UseCaseInvocationSerializer());
		deserializationModule.addDeserializer(Trace.class, new IITTraceImplDeserializer());
		deserializationModule.addDeserializer(SubTrace.class, new SubTraceImplDeserializer());
		deserializationModule.addDeserializer(RemoteInvocation.class, new RemoteInvocationDeserializer());
		deserializationModule.addDeserializer(HTTPRequestProcessing.class, new HTTPRequestProcessingDeserializer());
		deserializationModule.addDeserializer(MethodInvocation.class, new MethodInvocationDeserializer());


		objectMapper.registerModule(deserializationModule);
	}

	// Deserializer

	/**
	 * Deserializes {@link IITSpanCallable}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	class UseCaseInvocationSerializer extends JsonDeserializer<UseCaseInvocation> {

		public UseCaseInvocationSerializer() {
			super();
		}

		@Override
		public UseCaseInvocation deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode node = jp.getCodec().readTree(jp);
			UseCaseInvocationImpl useCaseInvocation = new UseCaseInvocationImpl(null, null, node.get("useCaseName").asText());
			if (null != node.get("children")) {
				for (JsonNode callableJson : node.get("children")) {
					if (callableJson.get("@class").asText().equals("org.spec.research.open.xtrace.api.core.callables.RemoteInvocation")) {
						useCaseInvocation.addCallee(objectMapper.treeToValue(callableJson, RemoteInvocation.class));
					} else if (callableJson.get("@class").asText().equals("org.spec.research.open.xtrace.api.core.callables.HTTPRequestProcessing")) {
						useCaseInvocation.addCallee(objectMapper.treeToValue(callableJson, HTTPRequestProcessing.class));
					} else if (callableJson.get("@class").asText().equals("org.spec.research.open.xtrace.api.core.callables.MethodInvocation")) {
						useCaseInvocation.addCallee(objectMapper.treeToValue(callableJson, MethodInvocation.class));
					} else if (callableJson.get("@class").asText().equals("org.spec.research.open.xtrace.api.core.callables.UseCaseInvocation")) {
						useCaseInvocation.addCallee(objectMapper.treeToValue(callableJson, UseCaseInvocation.class));
					}
				}
			}
			try {
				useCaseInvocation.setTimestamp(node.get("timeStamp").getLongValue());
				Iterator<String> nodeIterator = node.getFieldNames();

				// Add additional information
				while (nodeIterator.hasNext()) {
					String fieldName = nodeIterator.next();
					if (fieldName.contains("additionalInformation")) {
						useCaseInvocation.addAdditionalInformation(new AdditionalInformation() {

							@Override
							public Object getValue() {
								try {
									return objectMapper.treeToValue(node.get(fieldName), Object.class);
								} catch (IOException e) {
									e.printStackTrace();
									return null;
								}
							}

							@Override
							public String getName() {
								return fieldName.split("\\.")[1];
							}
						});
					}
				}
				useCaseInvocation.setResponseTime(node.get("responseTime").getLongValue());
				useCaseInvocation.setThreadID(node.get("threadID").getLongValue());
				useCaseInvocation.setThreadName(node.get("threadName").asText());
				useCaseInvocation.setIdentifier(objectMapper.treeToValue(node.get("identifier"), Object.class));

				List<String> labels = objectMapper.readValue(node.get("labels"), listTypRef);
				for (String label : labels) {
					useCaseInvocation.addLabel(label);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return useCaseInvocation;
		}
	}

	/**
	 * Deserializes {@link IITTraceImpl}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	class IITTraceImplDeserializer extends JsonDeserializer<Trace> {

		public IITTraceImplDeserializer() {
			super();
		}

		@Override
		public TraceImpl deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode node = jp.getCodec().readTree(jp);
			SubTraceImpl subTrace = objectMapper.treeToValue(node.get("rootOfTrace"), SubTraceImpl.class);
			TraceImpl traceImpl = new TraceImpl(node.get("traceID").getLongValue());
			traceImpl.setIdentifier(objectMapper.treeToValue(node.get("identifier"), Object.class));
			traceImpl.setRoot(subTrace);
			return traceImpl;
		}
	}

	/**
	 * Deserializes {@link IITSubTraceImpl}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	class SubTraceImplDeserializer extends JsonDeserializer<SubTrace> {

		public SubTraceImplDeserializer() {
			super();
		}

		@Override
		public SubTrace deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode node = jp.getCodec().readTree(jp);
			JsonNode callableNode = node.get("rootOfSubtrace");
			Callable callable = null;
			if (callableNode.get("@class").asText().equals("org.spec.research.open.xtrace.api.core.callables.RemoteInvocation")) {
				callable = objectMapper.treeToValue(callableNode, RemoteInvocation.class);
			} else if (callableNode.get("@class").asText().equals("org.spec.research.open.xtrace.api.core.callables.HTTPRequestProcessing")) {
				callable = objectMapper.treeToValue(callableNode, HTTPRequestProcessing.class);
			} else if (callableNode.get("@class").asText().equals("org.spec.research.open.xtrace.api.core.callables.MethodInvocation")) {
				callable = objectMapper.treeToValue(callableNode, MethodInvocation.class);
			} else if (callableNode.get("@class").asText().equals("org.spec.research.open.xtrace.api.core.callables.UseCaseInvocation")) {
				callable = objectMapper.treeToValue(callableNode, UseCaseInvocation.class);
			}
			SubTraceImpl subTraceImpl = new SubTraceImpl(node.get("subTraceId").getLongValue(), null, null);
			subTraceImpl.setIdentifier(objectMapper.treeToValue(node.get("identifier"), Object.class));
			TypeReference<ArrayList<SubTrace>> subTraceListType = new TypeReference<ArrayList<SubTrace>>() {
			};
			List<SubTraceImpl> childSubTraces = objectMapper.readValue(node.get("subtraces"), subTraceListType);

			// Add SubTraces
			for (SubTraceImpl childSubTrace : childSubTraces) {
				subTraceImpl.addSubTrace(childSubTrace);
			}
			subTraceImpl.setRoot((AbstractCallableImpl) callable);

			// Add location
			LocationImpl location = new LocationImpl(node.get("host").asText(), node.get("runtimeEnvironment").getTextValue(), node.get("application").getTextValue(), node.get("businessTransaction").getTextValue());
			location.setNodeType(node.get("nodeType").asText());
			location.setServerName(node.get("serverName").getTextValue());
			subTraceImpl.setLocation(location);

			return subTraceImpl;
		}
	}

	/**
	 * Deserializes {@link RemoteInvocation}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	class RemoteInvocationDeserializer extends JsonDeserializer<RemoteInvocation> {

		public RemoteInvocationDeserializer() {
			super();
		}

		@Override
		public RemoteInvocation deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode node = jp.getCodec().readTree(jp);
			RemoteInvocationImpl remoteInvocationImpl = new RemoteInvocationImpl(null, null);
			remoteInvocationImpl.setTimestamp(node.get("timeStamp").getLongValue());
			Iterator<String> nodeIterator = node.getFieldNames();

			// Add additional information
			while (nodeIterator.hasNext()) {
				String fieldName = nodeIterator.next();
				if (fieldName.contains("additionalInformation")) {
					remoteInvocationImpl.addAdditionalInformation(new AdditionalInformation() {

						@Override
						public Object getValue() {
							try {
								return objectMapper.treeToValue(node.get(fieldName), Object.class);
							} catch (IOException e) {
								e.printStackTrace();
								return null;
							}
						}

						@Override
						public String getName() {
							return fieldName.split("\\.")[1];
						}
					});
				}
			}
			remoteInvocationImpl.setResponseTime(node.get("responseTime").getLongValue());
			remoteInvocationImpl.setThreadID(node.get("threadID").getLongValue());
			remoteInvocationImpl.setThreadName(node.get("threadName").asText());
			remoteInvocationImpl.setIdentifier(objectMapper.treeToValue(node.get("identifier"), Object.class));

			List<String> labels = objectMapper.readValue(node.get("labels"), listTypRef);
			for (String label : labels) {
				remoteInvocationImpl.addLabel(label);
			}

			remoteInvocationImpl.setTargetSubTrace((SubTraceImpl) objectMapper.treeToValue(node.get("targetSubTrace"), SubTrace.class));

			return remoteInvocationImpl;
		}
	}

	/**
	 * Deserializes {@link HTTPRequestProcessing}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	class HTTPRequestProcessingDeserializer extends JsonDeserializer<HTTPRequestProcessing> {

		public HTTPRequestProcessingDeserializer() {
			super();
		}

		@Override
		public HTTPRequestProcessing deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode node = jp.getCodec().readTree(jp);
			HTTPRequestProcessingImpl httpRequestProcessing = new HTTPRequestProcessingImpl(null, null);
			httpRequestProcessing.setTimestamp(node.get("timeStamp").getLongValue());
			Iterator<String> nodeIterator = node.getFieldNames();

			// Add additional information
			while (nodeIterator.hasNext()) {
				String fieldName = nodeIterator.next();
				if (fieldName.contains("additionalInformation")) {
					httpRequestProcessing.addAdditionalInformation(new AdditionalInformation() {

						@Override
						public Object getValue() {
							try {
								return objectMapper.treeToValue(node.get(fieldName), Object.class);
							} catch (IOException e) {
								e.printStackTrace();
								return null;
							}
						}

						@Override
						public String getName() {
							return fieldName.split("\\.")[1];
						}
					});
				}
			}
			httpRequestProcessing.setResponseTime(node.get("responseTime").getLongValue());
			httpRequestProcessing.setThreadID(node.get("threadID").getLongValue());
			httpRequestProcessing.setThreadName(node.get("threadName").asText());
			httpRequestProcessing.setIdentifier(objectMapper.treeToValue(node.get("identifier"), Object.class));

			// Add labels
			List<String> labels = objectMapper.readValue(node.get("labels"), listTypRef);
			for (String label : labels) {
				httpRequestProcessing.addLabel(label);
			}

			httpRequestProcessing.setUri(node.get("uri").asText());


			httpRequestProcessing.setResponseHTTPHeaders(objectMapper.readValue(node.get("responseHTTPHeaders"), mapTypRef));
			httpRequestProcessing.setResponseCode(node.get("responseCode").getLongValue());
			httpRequestProcessing.setRequestMethod(objectMapper.treeToValue(node.get("requestMethod"), HTTPMethod.class));
			httpRequestProcessing.setHTTPAttributes(objectMapper.readValue(node.get("HTTPSessionAttributes"), mapTypRef));
			httpRequestProcessing.setHTTPParameters(objectMapper.readValue(node.get("HTTPParameters"), mapTypRef));
			httpRequestProcessing.setHTTPHeaders(objectMapper.readValue(node.get("HTTPHeaders"), mapTypRef));
			httpRequestProcessing.setHTTPAttributes(objectMapper.readValue(node.get("HTTPAttributes"), mapTypRef));

			return httpRequestProcessing;
		}
	}

	/**
	 * Deserializes {@link MethodInvocation}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	class MethodInvocationDeserializer extends JsonDeserializer<MethodInvocation> {

		public MethodInvocationDeserializer() {
			super();
		}

		@Override
		public MethodInvocation deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode node = jp.getCodec().readTree(jp);
			MethodInvocationImpl methodInvocation = new MethodInvocationImpl(null, null);
			methodInvocation.setTimestamp(node.get("timeStamp").getLongValue());
			Iterator<String> nodeIterator = node.getFieldNames();

			// Add additional information
			while (nodeIterator.hasNext()) {
				String fieldName = nodeIterator.next();
				if (fieldName.contains("additionalInformation")) {
					methodInvocation.addAdditionalInformation(new AdditionalInformation() {

						@Override
						public Object getValue() {
							try {
								return objectMapper.treeToValue(node.get(fieldName), Object.class);
							} catch (IOException e) {
								e.printStackTrace();
								return null;
							}
						}

						@Override
						public String getName() {
							return fieldName.split("\\.")[1];
						}
					});
				}
			}
			methodInvocation.setResponseTime(node.get("responseTime").getLongValue());
			methodInvocation.setThreadID(node.get("threadID").getLongValue());
			methodInvocation.setThreadName(node.get("threadName").asText());
			methodInvocation.setIdentifier(objectMapper.treeToValue(node.get("identifier"), Object.class));

			// Add labels

			List<String> labels = objectMapper.readValue(node.get("labels"), listTypRef);
			for (String label : labels) {
				methodInvocation.addLabel(label);
			}

			methodInvocation.setSyncTime(node.get("syncTime").getLongValue() == -1l ? Optional.empty() : Optional.of(node.get("syncTime").getLongValue()));
			methodInvocation.setSignature(node.get("signature").asText());
			methodInvocation.setReturnType(node.get("returnType").asText());
			methodInvocation.setReturnValue(
					objectMapper.treeToValue(node.get("returnValue"), Object.class) == null ? Optional.empty() : Optional.of(objectMapper.treeToValue(node.get("returnValue"), Object.class)));
			
			// Add parameterValues
			TypeReference<HashMap<Integer, String>> mapTypRef = new TypeReference<HashMap<Integer, String>>() {
			};
			HashMap<Integer, String> parameterValues = objectMapper.readValue(node.get("parameterValues"), mapTypRef);
			
			for (Integer key : parameterValues.keySet()) {
				methodInvocation.addParameterValue(key, parameterValues.get(key));
			}
			methodInvocation.setParameterTypes(objectMapper.readValue(node.get("parameterTypes"), listTypRef));
			methodInvocation.setPackageName(node.get("packageName").asText());
			methodInvocation.setMethodName(node.get("methodName").asText());
			methodInvocation.setGCTime(node.get("GCTime").getLongValue() == -1l ? Optional.empty() : Optional.of(node.get("GCTime").getLongValue()));
			methodInvocation.setCPUTime(node.get("CPUTime").getLongValue() == -1l ? Optional.empty() : Optional.of(node.get("CPUTime").getLongValue()));
			methodInvocation.setClassName(node.get("className").asText());

			return methodInvocation;
		}
	}
}
