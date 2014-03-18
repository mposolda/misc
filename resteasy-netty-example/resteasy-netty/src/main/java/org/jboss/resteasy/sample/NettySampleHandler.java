package org.jboss.resteasy.sample;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class NettySampleHandler extends SimpleChannelInboundHandler<DefaultHttpRequest> {

    private final String excludedPath;

    public NettySampleHandler(String excludedPath) {
        this.excludedPath = excludedPath;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultHttpRequest msg) throws Exception {
        URI uri = new URI( msg.getUri() );

        // Forward to resteasy handlers
        if (uri.getRawPath().startsWith(excludedPath)) {
            ctx.fireChannelRead(msg);
            return;
        }

        String headerNames = msg.headers().names().toString();

        String query = uri.getRawQuery();
        if ( query == null ) {
            query = "?";
        } else {
            query = "?" + query;
        }

        QueryStringDecoder decoder = new QueryStringDecoder(query);
        String paramNames = decoder.parameters().keySet().toString();

        ByteBuf content = createResponse(headerNames, paramNames);

        writeResponse(ctx, content, uri);
    }

    private void writeResponse(ChannelHandlerContext ctx, ByteBuf content, URI uri) {
        DefaultHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
        response.headers().add(HttpHeaders.Names.LOCATION, uri.toString());
        response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/html");

        ctx.writeAndFlush(response);
    }

    private ByteBuf createResponse(String headerNames, String paramNames) throws XMLStreamException {
        ByteBuf buffer = Unpooled.buffer();

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLEventWriter writer = factory.createXMLEventWriter(new ByteBufOutputStream(buffer));
        XMLEventFactory eventFactory = XMLEventFactory.newFactory();

        writer.add(eventFactory.createStartElement("", "", "HTML"));
        writer.add(eventFactory.createStartElement("", "", "BODY"));

        writer.add(eventFactory.createStartElement("", "", "B"));
        writer.add(eventFactory.createCharacters("header names: " + headerNames));
        writer.add(eventFactory.createEndElement("", "", "B"));

        writer.add(eventFactory.createStartElement("", "", "BR"));

        writer.add(eventFactory.createStartElement("", "", "B"));
        writer.add(eventFactory.createCharacters("param names: " + paramNames));
        writer.add(eventFactory.createEndElement("", "", "B"));

        writer.add(eventFactory.createEndElement("", "", "BODY"));
        writer.add(eventFactory.createEndElement("", "", "HTML"));

        writer.flush();
        writer.close();
        return buffer;
    }
}
