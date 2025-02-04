package io.mosip.authentication.demo.service.config;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BDBInfo;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.entities.RegistryIDType;
import io.mosip.kernel.core.util.CryptoUtil;

public class BIRDeserializer extends StdDeserializer<BIR> {
	
	ObjectMapper mapper = new ObjectMapper();
	
	public BIRDeserializer() {
		this(null);
	}

	protected BIRDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public BIR deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);

		RegistryIDType format = new RegistryIDType();
		format.setOrganization(node.get("bdbInfo").get("format").get("organization").asText());
		format.setType(node.get("bdbInfo").get("format").get("type").asText());
		return new BIR.BIRBuilder().withBdb(CryptoUtil.decodeBase64(node.get("bdb").asText()))
				.withBdbInfo(new BDBInfo.BDBInfoBuilder()
						.withType((List<BiometricType>) mapper.readValue(node.get("bdbInfo").get("type").toString(), List.class))
						.withSubtype(mapper.readValue(node.get("bdbInfo").get("subtype").toString(), List.class))
						.withFormat(format).build()).build();
	}

}
