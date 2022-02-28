package com.nphcswe.controller.error;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.nphcswe.helper.ResponseMessage;
import com.nphcswe.helper.exception.BadInputException;
import com.nphcswe.model.ResponseObject;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LogManager.getLogger();

	private ResponseEntity<Object> buildResponseEntity(ResponseObject obj, HttpStatus status) {
		return new ResponseEntity<>(obj, status);
	}

	@ExceptionHandler(BadInputException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleBadInputException(BadInputException ex) {
		logger.error(ex.getMessage(), ex);
		return buildResponseEntity(new ResponseObject(ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleAllException(Exception ex) {
		logger.error(ex.getMessage(), ex);
		return buildResponseEntity(new ResponseObject(ResponseMessage.MSG_ERR_UNKNOWN), HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		if (ex.getMessage().toLowerCase().contains(ResponseMessage.MSG_ERR_MISSING_REQUEST_BODY.toLowerCase())) {
			return buildResponseEntity(new ResponseObject(ResponseMessage.MSG_ERR_MISSING_REQUEST_BODY),
					HttpStatus.BAD_REQUEST);
		}

		else if (ex.getMessage().toLowerCase().contains("json parse error: unexpected character")) {
			return buildResponseEntity(new ResponseObject(ResponseMessage.MSG_ERR_REQUEST_INVALID),
					HttpStatus.BAD_REQUEST);
		}

		else if (ex.getMessage().toLowerCase()
				.contains("json parse error: unexpected end-of-input: expected close marker for object")) {
			return buildResponseEntity(new ResponseObject(ResponseMessage.MSG_ERR_REQUEST_INVALID),
					HttpStatus.BAD_REQUEST);
		}
		logger.error(ex.getMessage(), ex);

		return handleExceptionInternal(ex, null, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
		if (!CollectionUtils.isEmpty(mediaTypes)) {
			headers.setAccept(mediaTypes);
		}

		logger.error(ex.getMessage(), ex);

		if (ex.getMessage().toLowerCase().contains("not supported")
				&& ex.getMessage().toLowerCase().contains("content type")) {
			return buildResponseEntity(new ResponseObject(ResponseMessage.MSG_ERR_UNSUPPORT_CONTENT_TYPE),
					HttpStatus.BAD_REQUEST);
		}

		return handleExceptionInternal(ex, null, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		pageNotFoundLogger.warn(ex.getMessage());

		Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
		if (!CollectionUtils.isEmpty(supportedMethods)) {
			headers.setAllow(supportedMethods);
		}
		return handleExceptionInternal(ex, new ResponseObject(ResponseMessage.MSG_ERR_UNSUPPORT_REQUEST_METHOD),
				headers, status, request);
	}
}