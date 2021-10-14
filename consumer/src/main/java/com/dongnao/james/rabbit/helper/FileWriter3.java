package com.dongnao.james.rabbit.helper;


import cn.hutool.core.io.FileUtil;
import com.dongnao.james.rabbit.body.FileBlockBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于FileChannel的文件分块写入
 *
 * @author hling 2020/04/01
 */
public class FileWriter3 implements IFileWriter {
	private static Logger log = LoggerFactory.getLogger(FileWriter3.class);
    private static final int MAX_CHECK_CHOUNT = 10;
    private String messageid;
    private String bufferfilepath;
    private FileChannel bufferfile;
    private String controlfilepath;
    private FileChannel controlfile;
    private byte[] matrix;
    private ScheduledExecutorService activeTimer;
    private int activecheckcount;
    private boolean isActive;
    private byte[] lastmatrix;
    

//    public FileWriter3(CmdMessage filemessage) throws IOException {
//        this.filemessage = filemessage;
//        this.messageid = filemessage.getMessageId();
//        this.fileBody = (FileBody) filemessage.getBody();
//        this.bufferfilepath = DirHelper.INST.getReceiveBufferPath(messageid);
//    	this.bufferfile = FileChannel.open(Paths.get(bufferfilepath, ""), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
//    	this.controlfilepath = DirHelper.INST.getReceiveMatrixPath(messageid);
//        if (FileUtil.exist(controlfilepath)) {
//            this.matrix = FileUtil.readBytes(controlfilepath);
//            if (matrix.length != fileBody.getBlockNum()) {
//            	log.error("已有文件块标志长度({})与文件消息中的申明块数({})不同！文件ID：{}", matrix.length, fileBody.getBlockNum(), messageid);
//            }
//            this.controlfile = FileChannel.open(Paths.get(controlfilepath, ""), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
//        } else {
//            this.controlfile = FileChannel.open(Paths.get(controlfilepath, ""), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
//            this.matrix = new byte[fileBody.getBlockNum()];
//            this.controlfile.write(ByteBuffer.wrap(this.matrix), 0);
//        }
//        isActive = true;
//        activeTimer = ThreadPoolBuilder.scheduledPool()
//                .setThreadNamePrefix(ThreadExecutor.FILEWATCHER_EXECUTOR_NAME)
//                .build();
//        lastmatrix = this.matrix;
//        activeTimer.scheduleAtFixedRate(() -> {
//            if (!Arrays.equals(matrix, lastmatrix)) {
//                lastmatrix = matrix;
//                activecheckcount = 0;
//                isActive = true;
//                //更新状态
//                DBStoreServer.INST.updateReceiveMsgStatus(messageid, MsgStatus.BLOCK_RECEIVEING);
//                log.info("{}文件接收中", filemessage.getBaseInfo());
//            } else {
//                activecheckcount += 1;
//                //每分钟检测一下文件接收状态,重试MAX_CHECK_CHOUNT后认为不是活动状态了
//                if (activecheckcount > MAX_CHECK_CHOUNT) {
//                    isActive = false;
//                    log.info("{}文件接收中断,重试{}次后为未活动状态.", filemessage.getBaseInfo(), MAX_CHECK_CHOUNT);
//                    DBStoreServer.INST.updateReceiveMsgStatus(messageid, MsgStatus.BLOCK_INTERRUPT);
//                }
//            }
//        }, 0, 2, TimeUnit.MINUTES);
//    }

    public FileWriter3(){

    }
    /**
     * @return true数据传输完成
     * @throws Exception
     */
    @Override
    public boolean write(FileBlockBody body) throws Exception {
    	log.debug("收到文件块：{}", body.getBlockindex());
//        synchronized (this.bufferfile) {
        	try {
        		bufferfile.write(ByteBuffer.wrap(body.getContent(), 0, body.getLimit()), body.getStart());
        	} catch (IOException e) {
        		// 文件已经写入完成并关闭后，如果还收到数据块，此处会因文件已关闭而抛出异常，该异常可忽略
        		if (!isComplete(this.matrix)) {
        			log.warn("接收到文件块(编号.{})时文件已经被关闭。", body.getBlockindex());
        			throw e;
        		} else {
        			return true;
        		}
        	}
//        }
        return this.writeControlFile(body.getBlockindex());
    }
    
    private boolean writeControlFile(long blockindex) throws IOException {
        int index = (int) blockindex;
        try {
        	this.controlfile.transferFrom(DummyFlagReadChannel.INST, blockindex, 1);
        	this.matrix[index] = 1;
        } catch (IOException e) {
        	// 如果存在相同块被重传的现象，有可能其他线程已经接收完并关闭文件，此时异常可以忽略
        	if (isComplete(this.matrix)) {
        		return true;
        	} else {
        		throw e;
        	}
        }
		if (!isComplete(this.matrix)) {
			return false;
		}
		// 接收完毕
		synchronized (this.controlfile) {
			// 其他线程已经完成写入并关闭
			if (!controlfile.isOpen()) {
				return true;
			}
			log.debug("文件{}接收第{}号文件块后全部接收完毕", this.messageid, blockindex);
			log.info("接收文件{}完成，共{}个文件块", this.messageid, matrix.length);
			try {
				activeTimer.shutdown();
				bufferfile.close();
				controlfile.close();
			} catch (IOException e) {
                Object filemessage;
                log.error("{}文件关闭操作失败", e);
			}
			return true;
		}
    }

    private boolean isComplete(byte[] matrix) {
        if (matrix != null && matrix.length > 0) {
            for (byte b : matrix) {
                if (b == 0) {
                    return false;
                }
            }
        }
        return true;
    }

//    @Override
//    public CmdMessage getFileMessage() {
//        return filemessage;
//    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void close(){
        try{
        	bufferfile.close();
        	controlfile.close();
        	activeTimer.shutdown();
        }catch (Exception e){
            log.error("{}释放文件资源出错");
        }


    }
    
    /**
     * 永远输出字节1的虚拟字节缓冲区，使用它可避免反复创建临时缓冲区只为了输出标志1
     * @author hling
     */
    public static class DummyFlagReadChannel implements ReadableByteChannel {
    	public final static DummyFlagReadChannel INST = new DummyFlagReadChannel(); 
    	
    	private boolean isOpen = true;
    	
		@Override
		public boolean isOpen() {
			return isOpen;
		}

		@Override
		public void close() throws IOException {
			isOpen = false;
		}

		@Override
		public int read(ByteBuffer dst) throws IOException {
			if (!isOpen) {
				return -1;
			}
			int len = dst.remaining();
			while (dst.remaining() > 0) {
				dst.put((byte)1);
			}
			return len;
		}
    }

	public static void main(String[] args) throws IOException {
		String filePath = "E:\\s.matrix";
		FileChannel bufferfile = FileChannel.open(Paths.get(filePath, ""), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		byte[] matrix = new byte[10];
		matrix[1] = 1;
		bufferfile.write(ByteBuffer.wrap(matrix), 0);
		byte[] matrix1 = FileUtil.readBytes(filePath);
		for(byte a : matrix1){
			System.out.println(a);
		}
	}

}
