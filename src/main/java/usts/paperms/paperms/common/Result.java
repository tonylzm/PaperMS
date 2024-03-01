package usts.paperms.paperms.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class Result<T> {
    @Setter
    @Getter
    private int code;
    @Setter
    @Getter
    private String message;
    @Setter
    @Getter
    private T body;

    private HttpStatus status;
    private HttpHeaders headers;


    public Result(HttpStatus status, HttpHeaders headers, T body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }
    public Result(int code, String message, T body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }
    // 构造函数
    public static <T> Result<T> success(T body) {
        return new Result<>(200, "操作成功", body);
    }
    //失败返回500
    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null);
    }
    // 返回一个成功的响应体
    // 返回一个成功的响应体
    public static <T> Result<T> ok(T body) {
        HttpHeaders headers = new HttpHeaders();
        return new Result<>(HttpStatus.OK, headers, body);
    }

    // 设置响应体的内容类型
    public Result<T> contentType(MediaType mediaType) {
        this.headers.setContentType(mediaType);
        return this;
    }

    // 设置响应头信息
    public Result<T> header(String headerName, String headerValue) {
        this.headers.add(headerName, headerValue);
        return this;
    }

    // 设置响应体
    public Result<T> body(T body) {
        this.body = body;
        return this;
    }

}
