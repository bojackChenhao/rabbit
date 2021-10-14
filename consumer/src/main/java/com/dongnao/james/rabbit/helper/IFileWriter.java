package com.dongnao.james.rabbit.helper;


import com.dongnao.james.rabbit.body.FileBlockBody;

public interface IFileWriter {

	public boolean isActive();

	public void close();


	public boolean write(FileBlockBody body) throws Exception;
}
