package cn.px.module.mould.mouldProductionPlan.service;

import com.alibaba.excel.EasyExcel;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

public class EasyExcelUtil {
    public static <T> void download(HttpServletResponse response, List<?> list, Class<T> tClass, String fileName, String sheetName) throws IOException {

        //如果 fileName 中包含空格字符，那么这些字符会被编码成 %20，因为在 URL 中空格字符必须被转义成 %20 才能正确传输
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        //必须：设置文件响应格式为二进制二进制流数据类型
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        //必须：设置 HTTP 响应的字符编码，以便客户端能够正确地解码和显示接收到的文本数据
        response.setCharacterEncoding("utf-8");
        //必须：通过设置 Content-Disposition 头部，告诉客户端如何处理接收到的响应内容，以及指定附件的文件名
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), tClass).registerConverter(new TimestampConverter()).sheet(sheetName).doWrite(list);
    }
}
