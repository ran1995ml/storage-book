package com.ran.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * TCPServer
 *
 * @author rwei
 * @since 2023/12/7 15:20
 */
public class TCPServer {
    public static void main(String[] args) {
        //只处理连接请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理业务请求
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128) //client请求速率大于server接收速率，使用该队列做缓冲
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("server is ready...");
            ChannelFuture channelFuture = bootstrap.bind(8888).sync();
            //监听通道关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 自定义Handler
     */
    static class NettyServerHandler extends ChannelInboundHandlerAdapter {
        /**
         * 通道有数据可读时执行
         * @param ctx 上下文对象，获得Pipeline、Channel、客户端地址
         * @param msg 客户端发送的数据
         * @throws Exception exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println(ctx.channel().remoteAddress() + ":" + byteBuf.toString());
            ctx.channel().eventLoop().execute(() -> {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName());
            });
        }

        /**
         * 数据读取完毕后执行
         * @param ctx 上下文对象，获得Pipeline、Channel、客户端地址
         */
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.writeAndFlush(Unpooled.copiedBuffer("read completed", CharsetUtil.UTF_8));
        }

        /**
         * 发生异常时执行
         * @param ctx 上下文对象，获得Pipeline、Channel、客户端地址
         * @param cause 异常对象
         * @throws Exception exception
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.channel().close();
        }
    }
}
