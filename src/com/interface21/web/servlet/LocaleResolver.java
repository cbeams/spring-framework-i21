package com.interface21.web.servlet;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: jho
 * Date: 27.02.2003
 */
public interface LocaleResolver {

  Locale resolveLocale(HttpServletRequest request);

  void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale);
}
