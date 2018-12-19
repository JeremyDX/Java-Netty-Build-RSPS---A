/*
* @ Author - The Netty Website Documentations.
* Returns A Pipeline which will be used for every connection to the server. 
*/

package com;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import com.codec.ChannelHandler;
import com.codec.RSProtocolDecoder;
import com.codec.RSEncoder;

public class PipelineFactory implements ChannelPipelineFactory {

	@Override
	public ChannelPipeline getPipeline() {
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("decoder", new RSProtocolDecoder());
		pipeline.addLast("encoder", new RSEncoder());
		pipeline.addLast("handler", new ChannelHandler());
		return pipeline;
	}

}
