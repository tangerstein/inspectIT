package rocks.inspectit.server.deserialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.esotericsoftware.kryo.io.Input;

import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.all.spring.logger.Log;
import rocks.inspectit.shared.all.storage.serializer.impl.SerializationManager;
import rocks.inspectit.shared.all.storage.serializer.provider.SerializationManagerProvider;
import rocks.inspectit.shared.all.storage.serializer.schema.ClassSchemaManager;
import rocks.inspectit.shared.all.storage.serializer.util.KryoUtil;

@Component
public class ItdataDeserializer {

	/**
	 * Deserializes a given itdata file.
	 */
	public static void deserializeInvocationSequences() {
		SerializationManager serializer = new SerializationManager();
		ClassSchemaManager classSchemaManager = new ClassSchemaManager();
		try {
			classSchemaManager.loadSchemasFromLocations();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		serializer.setSchemaManager(classSchemaManager);
		serializer.initKryo();

		List<InvocationSequenceData> receivedData = new ArrayList<InvocationSequenceData>();
		String[] itdatas = new String[] {"-197127687.itdata", "-314493660.itdata"};
		try {
			for (int i = 0; i < itdatas.length; i++) {
				InputStream inputStream = new FileInputStream(new File(itdatas[i]));
				Input input = new Input(inputStream);
				while (KryoUtil.hasMoreBytes(input)) {
					Object object = serializer.deserialize(input);
					InvocationSequenceData element = (InvocationSequenceData) object;
					receivedData.add(element);
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		ItdataDeserializer.deserializeInvocationSequences();
	}
}
