package rocks.inspectit.server.service.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.spec.research.open.xtrace.api.core.Trace;
import org.spec.research.open.xtrace.dflt.impl.serialization.OPENxtraceSerializationFactory;
import org.spec.research.open.xtrace.dflt.impl.serialization.OPENxtraceSerializationFormat;
import org.spec.research.open.xtrace.dflt.impl.serialization.OPENxtraceSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import rocks.inspectit.server.service.OPENxtraceAccessService;

@Controller
@RequestMapping(value = "open-xtrace")
public class OPENxtraceRestfulService {

	@Autowired
	private OPENxtraceAccessService openxtraceAccessService;
	private OPENxtraceSerializer serializer;

	/**
	 * Restful endpoint to get traces, which are in the buffer as OPEN.xtrace.
	 * 
	 * @param fromDate
	 *            earliest date
	 * @param toDate
	 *            latest date
	 * @return JSON formatted openXTRACE
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RequestMapping(value = "/get", method = GET)
	@ResponseBody
	public String getOpenXTRACETraces(@RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Date fromDate,
			@RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Date toDate) throws JsonGenerationException, JsonMappingException, IOException {
		List<Trace> traces = openxtraceAccessService.getOpenXTRACETraces(-1, fromDate, toDate, null);
		OutputStream stream = new ByteArrayOutputStream();
		serializer.prepare(stream);
		for (Trace trace : traces) {
			serializer.writeTrace(trace);
		}
		serializer.close();

		// Convert the outputstream to a json array
		BufferedReader bufReader = new BufferedReader(new StringReader(stream.toString()));
		String line = null;
		ArrayNode jsonArray = new ArrayNode(JsonNodeFactory.instance);
		ObjectMapper mapper = new ObjectMapper();
		while ((line = bufReader.readLine()) != null) {
			jsonArray.add(mapper.readTree(line));
		}
		return jsonArray.toString();
	}

	/**
	 * Restful endpoint to get a specific trace, which is in the buffer as OPEN.xtrace.
	 * 
	 * @param
	 * @return JSON formatted openXTRACE
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RequestMapping(value = "/get/{id}", method = GET)
	@ResponseBody
	public String getOpenXTRACETrace(@PathVariable("id") String id) throws JsonGenerationException, JsonMappingException, IOException {
		Trace trace = openxtraceAccessService.getOpenXTRACETrace(Long.parseLong(id, 16));
		OutputStream stream = new ByteArrayOutputStream();
		serializer.prepare(stream);
		serializer.writeTrace(trace);
		serializer.close();

		// Convert the outputstream to a json array
		BufferedReader bufReader = new BufferedReader(new StringReader(stream.toString()));
		String line = null;
		ArrayNode jsonArray = new ArrayNode(JsonNodeFactory.instance);
		ObjectMapper mapper = new ObjectMapper();
		while ((line = bufReader.readLine()) != null) {
			jsonArray.add(mapper.readTree(line));
		}
		return jsonArray.toString();
	}

	/**
	 * Header information for swagger requests.
	 *
	 * @param response
	 *            Response information
	 */
	@ModelAttribute
	public void setVaryResponseHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

	}

	@PostConstruct
	private void initialize() {
		serializer = OPENxtraceSerializationFactory.getInstance().getSerializer(OPENxtraceSerializationFormat.JSON);
	}

}
