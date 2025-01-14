package cn.px.module.general.service;

import cn.px.module.general.bean.VerifyCode;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author 品讯科技
 */
public interface VerifyCodeGen {
	/**
	* 生成验证码并返回code，将图片写的os中
	*
	* @param width 图片宽都
	* @param height 图片高度
	* @param os 输出流
	* @return Base64 字符串对象
	* @throws IOException IO 异常
	*/
	String generate(int width, int height, OutputStream os) throws IOException;

	/**
	 * 生成验证码并返回code，将图片写的os中
	 *
	 * @param width 图片宽都
	 * @param height 图片高度
	 * @return VerifyCode 对象
	 * @throws IOException IO 异常
	 */
	VerifyCode generate(int width, int height) throws IOException;

}
