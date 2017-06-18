package com.lingchang.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by cong on 2017/6/18.
 */
public class UploadServer extends Thread {

    //��ʵ��
    private static UploadServer dbServer = null;

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;

    //����ʵ��
    public static UploadServer newBuild() {
        if (dbServer == null) {
            dbServer = new UploadServer();
        }
        return dbServer;
    }

    public void run() {
        try {
            startServer();
        } catch (Exception e) {
            System.out.println("���ݷ������������쳣��" + e.toString());
            e.printStackTrace();
        }
    }

    private void startServer() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup);

            b.option(ChannelOption.TCP_NODELAY, true);
            b.option(ChannelOption.SO_TIMEOUT, 60000);
            b.option(ChannelOption.SO_SNDBUF, 1048576 * 200);

            b.option(ChannelOption.SO_KEEPALIVE, true);

            b.channel(NioServerSocketChannel.class);
            b.childHandler(new UpLoadServerInitializer());

            // �������󶨶˿ڼ���
            ChannelFuture f = b.bind("localhost", 3306).sync();

            System.out.println("���ݷ���" +  "localhost:3306  �������...");
            // �����������رռ���
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}