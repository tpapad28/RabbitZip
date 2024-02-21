package com.tpapad.rabbitzip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Component
@Slf4j
public class CompressedMessageConverter implements MessageConverter {

    final MessageConverter simpleConverter = new SimpleMessageConverter();

    @Override
    public Message toMessage(final Object o, final MessageProperties messageProperties)
            throws MessageConversionException {

        if (!(o instanceof String)) {
            throw new MessageConversionException("...");
        }
        final byte[] message = ((String) o).getBytes(StandardCharsets.UTF_8);

        final int BUFFER_SIZE = 8 << 10;// 8K should be ok
        ByteArrayOutputStream rstBao = new ByteArrayOutputStream(BUFFER_SIZE);
        try {
            ConfigurableGZIPOutputStream zos = new ConfigurableGZIPOutputStream(rstBao,
                    BUFFER_SIZE).withBestCompression();
            zos.write(message);
            zos.flush();
            zos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final byte[] compressedMessage = rstBao.toByteArray();
        log.info(
                "Compressed Length: " + compressedMessage.length + " vs Message Length: " + message.length + " / Ratio: " +
                        String.format("%.2f%%", compressedMessage.length * 100f / message.length));

        // This is the discriminator
        messageProperties.getHeaders().put("Use-Gzip", true);

        return new Message(compressedMessage, messageProperties);
    }

    @Override
    public Object fromMessage(final Message message) throws MessageConversionException {
        message.getMessageProperties().getHeaders().forEach((key, value) -> log.info(key + ":" + value));
        final boolean useGzip = (boolean) message.getMessageProperties().getHeaders().computeIfAbsent("Use-Gzip",
                x -> false);
        if (useGzip) {
            try {
                // @formatter:off
				return new BufferedReader(
					new InputStreamReader(
						new GZIPInputStream(
							new ByteArrayInputStream(message.getBody()))))
					.lines()
					.collect(Collectors.joining(System.lineSeparator()));
				// @formatter:on
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return simpleConverter.fromMessage(message);
        }
    }
}
