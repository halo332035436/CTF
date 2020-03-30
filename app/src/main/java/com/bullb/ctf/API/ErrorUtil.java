package com.bullb.ctf.API;


import com.bullb.ctf.API.Response.ErrorResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by oscar on 21/4/16.
 */
public class ErrorUtil {
    public static ErrorResponse parseError(Response<?> response) {
        Converter<ResponseBody, ErrorResponse> converter = ServiceGenerator.builder.build().responseBodyConverter(ErrorResponse.class,new Annotation[0]);
        ErrorResponse error;
        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return null;
        }
        return error;
    }
}
