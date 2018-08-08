/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.xpath;

import ddf.catalog.filter.ArgumentBuilder;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.FilterDelegate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.opengis.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XpathFilterDelegate extends FilterDelegate<Filter> {
  protected FilterBuilder filterBuilder;

  private static final Logger LOGGER = LoggerFactory.getLogger(XpathFilterDelegate.class);

  private HashMap<Matcher, String> replacementTable;

  // Prevent usage of default constructor
  private XpathFilterDelegate() {}

  public XpathFilterDelegate(FilterBuilder filterBuilder, HashMap<Matcher, String> table) {
    this.filterBuilder = filterBuilder;
    this.replacementTable = table;
  }

  // Logical operators
  @Override
  public Filter include() {
    return Filter.INCLUDE;
  }

  @Override
  public Filter exclude() {
    return Filter.EXCLUDE;
  }

  @Override
  public Filter not(Filter filter) {
    return filterBuilder.not(filter);
  }

  @Override
  public Filter and(List<Filter> filters) {
    return filterBuilder.allOf(filters);
  }

  @Override
  public Filter or(List<Filter> filters) {
    return filterBuilder.anyOf(filters);
  }

  // PropertyIsNull
  @Override
  public Filter propertyIsNull(String propertyName) {
    return filterBuilder.attribute(propertyName).is().empty();
  }

  // PropertyIsLike
  @Override
  public Filter propertyIsLike(String propertyName, String pattern, boolean isCaseSensitive) {
    if (isCaseSensitive) {
      return filterBuilder.attribute(propertyName).is().like().caseSensitiveText(pattern);
    }
    return filterBuilder.attribute(propertyName).is().like().text(pattern);
  }

  // PropertyIsFuzzy
  @Override
  public Filter propertyIsFuzzy(String propertyName, String literal) {
    return filterBuilder.attribute(propertyName).is().like().fuzzyText(literal);
  }

  // PropertyIsEqualTo
  @Override
  public Filter propertyIsEqualTo(String propertyName, String literal, boolean isCaseSensitive) {
    return filterBuilder.attribute(propertyName).equalTo().text(literal);
  }

  @Override
  public Filter propertyIsEqualTo(String functionName, List<Object> arguments, Object literal) {
    // Making an assumption that the first argument will be an attribute followed by N values.
    // This will work for most functions. If it doesn't a switch based on function name can be added
    // to handle a specific function here.
    ArgumentBuilder argBuilder = filterBuilder.function(functionName);
    for (int i = 0; i < arguments.size(); i++) {
      if (i == 0) {
        argBuilder.attributeArg(arguments.get(0).toString());
      } else {
        argBuilder.objArg(arguments.get(i));
      }
    }

    if (literal instanceof String) {
      return argBuilder.equalTo().text(literal.toString());
    } else if (literal instanceof Boolean) {
      return argBuilder.equalTo().bool((Boolean) literal);
    } else if (literal instanceof Long) {
      return argBuilder.equalTo().number((Long) literal);
    } else if (literal instanceof Integer) {
      return argBuilder.equalTo().number((Integer) literal);
    } else if (literal instanceof Double) {
      return argBuilder.equalTo().number((Double) literal);
    } else if (literal instanceof Float) {
      return argBuilder.equalTo().number((Float) literal);
    } else if (literal instanceof Short) {
      return argBuilder.equalTo().number((Short) literal);
    } else if (literal instanceof Date) {
      return argBuilder.equalTo().date((Date) literal);
    }

    throw new UnsupportedOperationException(
        "propertyIsEqualTo(String,List,Object) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter propertyIsEqualTo(String propertyName, Date literal) {
    return filterBuilder.attribute(propertyName).equalTo().date(literal);
  }

  @Override
  public Filter propertyIsEqualTo(String propertyName, Date startDate, Date endDate) {
    throw new UnsupportedOperationException(
        "propertyIsEqualTo(String,Date,Date) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter propertyIsEqualTo(String propertyName, int literal) {
    return filterBuilder.attribute(propertyName).equalTo().number(literal);
  }

  @Override
  public Filter propertyIsEqualTo(String propertyName, short literal) {
    return filterBuilder.attribute(propertyName).equalTo().number(literal);
  }

  @Override
  public Filter propertyIsEqualTo(String propertyName, long literal) {
    return filterBuilder.attribute(propertyName).equalTo().number(literal);
  }

  @Override
  public Filter propertyIsEqualTo(String propertyName, double literal) {
    return filterBuilder.attribute(propertyName).equalTo().number(literal);
  }

  @Override
  public Filter propertyIsEqualTo(String propertyName, float literal) {
    return filterBuilder.attribute(propertyName).equalTo().number(literal);
  }

  @Override
  public Filter propertyIsEqualTo(String propertyName, byte[] literal) {
    return filterBuilder.attribute(propertyName).equalTo().bytes(literal);
  }

  @Override
  public Filter propertyIsEqualTo(String propertyName, boolean literal) {
    return filterBuilder.attribute(propertyName).equalTo().bool(literal);
  }

  @Override
  public Filter propertyIsEqualTo(String propertyName, Object literal) {
    throw new UnsupportedOperationException(
        "propertyIsEqualTo(String,Object) not supported by CopyFilterDelegate.");
  }

  // PropertyIsNotEqualTo
  @Override
  public Filter propertyIsNotEqualTo(String propertyName, String literal, boolean isCaseSensitive) {
    return filterBuilder.attribute(propertyName).notEqualTo().text(literal);
  }

  @Override
  public Filter propertyIsNotEqualTo(String propertyName, Date literal) {
    return filterBuilder.attribute(propertyName).notEqualTo().date(literal);
  }

  @Override
  public Filter propertyIsNotEqualTo(String propertyName, Date startDate, Date endDate) {
    throw new UnsupportedOperationException(
        "propertyIsNotEqualTo(String,Date,Date) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter propertyIsNotEqualTo(String propertyName, int literal) {
    return filterBuilder.attribute(propertyName).notEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsNotEqualTo(String propertyName, short literal) {
    return filterBuilder.attribute(propertyName).notEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsNotEqualTo(String propertyName, long literal) {
    // TODO: is it ok to convert long to int here? alternatives?
    return filterBuilder.attribute(propertyName).notEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsNotEqualTo(String propertyName, double literal) {
    return filterBuilder.attribute(propertyName).notEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsNotEqualTo(String propertyName, float literal) {
    return filterBuilder.attribute(propertyName).notEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsNotEqualTo(String propertyName, byte[] literal) {
    return filterBuilder.attribute(propertyName).notEqualTo().bytes(literal);
  }

  @Override
  public Filter propertyIsNotEqualTo(String propertyName, boolean literal) {
    return filterBuilder.attribute(propertyName).notEqualTo().bool(literal);
  }

  @Override
  public Filter propertyIsNotEqualTo(String propertyName, Object literal) {
    // TODO
    throw new UnsupportedOperationException(
        "propertyIsNotEqualTo(String,Object) not supported by CopyFilterDelegate.");
  }

  // PropertyIsGreaterThan
  @Override
  public Filter propertyIsGreaterThan(String propertyName, String literal) {
    throw new UnsupportedOperationException(
        "propertyIsGreaterThan(String,String) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter propertyIsGreaterThan(String propertyName, Date literal) {
    // Because after() is inclusive of the date; use GreaterThanOrEqualTo
    throw new UnsupportedOperationException(
        "propertyIsGreaterThan(String,Date) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter propertyIsGreaterThan(String propertyName, int literal) {
    return filterBuilder.attribute(propertyName).greaterThan().number(literal);
  }

  @Override
  public Filter propertyIsGreaterThan(String propertyName, short literal) {
    return filterBuilder.attribute(propertyName).greaterThan().number(literal);
  }

  @Override
  public Filter propertyIsGreaterThan(String propertyName, long literal) {
    return filterBuilder.attribute(propertyName).greaterThan().number(literal);
  }

  @Override
  public Filter propertyIsGreaterThan(String propertyName, double literal) {
    return filterBuilder.attribute(propertyName).greaterThan().number(literal);
  }

  @Override
  public Filter propertyIsGreaterThan(String propertyName, float literal) {
    return filterBuilder.attribute(propertyName).greaterThan().number(literal);
  }

  @Override
  public Filter propertyIsGreaterThan(String propertyName, Object literal) {
    throw new UnsupportedOperationException(
        "propertyIsGreaterThan(String,Object) not supported by CopyFilterDelegate.");
  }

  // PropertyIsGreaterThanOrEqualTo
  @Override
  public Filter propertyIsGreaterThanOrEqualTo(String propertyName, String literal) {
    throw new UnsupportedOperationException(
        "propertyIsGreaterThanOrEqualTo(String,String) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter propertyIsGreaterThanOrEqualTo(String propertyName, Date literal) {
    throw new UnsupportedOperationException(
        "propertyIsGreaterThanOrEqualTo(String,Date) not supported by CopyFilterDelegate. Should use after() for Date comparisons.");
  }

  @Override
  public Filter propertyIsGreaterThanOrEqualTo(String propertyName, int literal) {
    return filterBuilder.attribute(propertyName).greaterThanOrEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsGreaterThanOrEqualTo(String propertyName, short literal) {
    return filterBuilder.attribute(propertyName).greaterThanOrEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsGreaterThanOrEqualTo(String propertyName, long literal) {
    return filterBuilder.attribute(propertyName).greaterThanOrEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsGreaterThanOrEqualTo(String propertyName, double literal) {
    return filterBuilder.attribute(propertyName).greaterThanOrEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsGreaterThanOrEqualTo(String propertyName, float literal) {
    return filterBuilder.attribute(propertyName).greaterThanOrEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsGreaterThanOrEqualTo(String propertyName, Object literal) {
    throw new UnsupportedOperationException(
        "propertyIsGreaterThanOrEqualTo(String,Object) not supported by CopyFilterDelegate.");
  }

  // PropertyIsLessThan
  @Override
  public Filter propertyIsLessThan(String propertyName, String literal) {
    throw new UnsupportedOperationException(
        "propertyIsLessThan(String,String) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter propertyIsLessThan(String propertyName, Date literal) {
    // Because before() is inclusive of the date
    throw new UnsupportedOperationException(
        "propertyIsLessThan(String,Date) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter propertyIsLessThan(String propertyName, int literal) {
    return filterBuilder.attribute(propertyName).lessThan().number(literal);
  }

  @Override
  public Filter propertyIsLessThan(String propertyName, short literal) {
    return filterBuilder.attribute(propertyName).lessThan().number(literal);
  }

  @Override
  public Filter propertyIsLessThan(String propertyName, long literal) {
    return filterBuilder.attribute(propertyName).lessThan().number(literal);
  }

  @Override
  public Filter propertyIsLessThan(String propertyName, double literal) {
    return filterBuilder.attribute(propertyName).lessThan().number(literal);
  }

  @Override
  public Filter propertyIsLessThan(String propertyName, float literal) {
    return filterBuilder.attribute(propertyName).lessThan().number(literal);
  }

  @Override
  public Filter propertyIsLessThan(String propertyName, Object literal) {
    throw new UnsupportedOperationException(
        "propertyIsLessThan(String,Object) not supported by CopyFilterDelegate.");
  }

  // PropertyIsLessThanOrEqualTo
  @Override
  public Filter propertyIsLessThanOrEqualTo(String propertyName, String literal) {
    throw new UnsupportedOperationException(
        "propertyIsLessThanOrEqualTo(String,String) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter propertyIsLessThanOrEqualTo(String propertyName, Date literal) {
    throw new UnsupportedOperationException(
        "propertyIsLessThanOrEqualTo(String,Date) not supported by CopyFilterDelegate. Should use before() for Date comparisons.");
  }

  @Override
  public Filter propertyIsLessThanOrEqualTo(String propertyName, int literal) {
    return filterBuilder.attribute(propertyName).lessThanOrEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsLessThanOrEqualTo(String propertyName, short literal) {
    return filterBuilder.attribute(propertyName).lessThanOrEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsLessThanOrEqualTo(String propertyName, long literal) {
    return filterBuilder.attribute(propertyName).lessThanOrEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsLessThanOrEqualTo(String propertyName, double literal) {
    return filterBuilder.attribute(propertyName).lessThanOrEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsLessThanOrEqualTo(String propertyName, float literal) {
    return filterBuilder.attribute(propertyName).lessThanOrEqualTo().number(literal);
  }

  @Override
  public Filter propertyIsLessThanOrEqualTo(String propertyName, Object literal) {
    throw new UnsupportedOperationException(
        "propertyIsLessThanOrEqualTo(String,Object) not supported by CopyFilterDelegate.");
  }

  // PropertyIsBetween
  @Override
  public Filter propertyIsBetween(String propertyName, String lowerBoundary, String upperBoundary) {
    throw new UnsupportedOperationException(
        "propertyIsBetween(String,String lowerBoundary,String upperBoundary) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter propertyIsBetween(String propertyName, Date lowerBoundary, Date upperBoundary) {
    throw new UnsupportedOperationException(
        "propertyIsBetween(String,Date,Date) not supported by CopyFilterDelegate. Should use during() for Date comparisons.");
  }

  @Override
  public Filter propertyIsBetween(String propertyName, int lowerBoundary, int upperBoundary) {
    return filterBuilder.attribute(propertyName).between().numbers(lowerBoundary, upperBoundary);
  }

  @Override
  public Filter propertyIsBetween(String propertyName, short lowerBoundary, short upperBoundary) {
    return filterBuilder.attribute(propertyName).between().numbers(lowerBoundary, upperBoundary);
  }

  @Override
  public Filter propertyIsBetween(String propertyName, long lowerBoundary, long upperBoundary) {
    return filterBuilder.attribute(propertyName).between().numbers(lowerBoundary, upperBoundary);
  }

  @Override
  public Filter propertyIsBetween(String propertyName, float lowerBoundary, float upperBoundary) {
    return filterBuilder.attribute(propertyName).between().numbers(lowerBoundary, upperBoundary);
  }

  @Override
  public Filter propertyIsBetween(String propertyName, double lowerBoundary, double upperBoundary) {
    return filterBuilder.attribute(propertyName).between().numbers(lowerBoundary, upperBoundary);
  }

  @Override
  public Filter propertyIsBetween(String propertyName, Object lowerBoundary, Object upperBoundary) {
    throw new UnsupportedOperationException(
        "propertyIsBetween(String,Object) not supported by CopyFilterDelegate.");
  }

  // XpathExists
  @Override
  public Filter xpathExists(String xpathExpression) {
    LOGGER.trace("in xpathExists - xpath: {}", xpathExpression);
    String replacement = findReplacement(xpathExpression);
    Filter modifiedFilter;
    if (replacement != null) {
      LOGGER.debug("Replacement value for {} found: {} ", xpathExpression, replacement);
      Filter emptyFilter = filterBuilder.attribute(replacement).is().empty();
      modifiedFilter = filterBuilder.not(emptyFilter);
    } else {
      LOGGER.debug("No replacement value found for {}", xpathExpression);
      modifiedFilter = filterBuilder.xpath(xpathExpression).exists();
    }
    return modifiedFilter;
  }

  // XpathIsLike
  @Override
  public Filter xpathIsLike(String xpathExpression, String pattern, boolean isCaseSensitive) {
    LOGGER.trace("in xpathIsLike - xpath: {}  pattern: {}", xpathExpression, pattern);
    String replacement = findReplacement(xpathExpression);
    Filter modifiedFilter;
    if (replacement != null) {
      LOGGER.debug("Replacement value for {} found: {}", xpathExpression, replacement);
      // found replacement property for specified xpath
      if (isCaseSensitive) {
        modifiedFilter =
            filterBuilder.attribute(replacement).is().like().caseSensitiveText(pattern);
      } else {
        modifiedFilter = filterBuilder.attribute(replacement).is().like().text(pattern);
      }
    } else {
      LOGGER.debug("No replacement value found for {}", xpathExpression);
      // no match found, just return the xpath filter
      if (isCaseSensitive) {
        modifiedFilter =
            filterBuilder.xpath(xpathExpression).is().like().caseSensitiveText(pattern);
      } else {
        modifiedFilter = filterBuilder.xpath(xpathExpression).is().like().text(pattern);
      }
    }
    return modifiedFilter;
  }

  // XpathIsFuzzy
  @Override
  public Filter xpathIsFuzzy(String xpathExpression, String literal) {
    LOGGER.trace("in xpathIsFuzzy - xpath: {}  literal: {}", xpathExpression, literal);
    String replacement = findReplacement(xpathExpression);
    Filter modifiedFilter;
    if (replacement != null) {
      // found replacement property for specified xpath
      LOGGER.trace("Replacement value for {} found: {} ", xpathExpression, replacement);
      modifiedFilter = filterBuilder.attribute(replacement).is().like().fuzzyText(literal);
    } else {
      // no replacement found - just return equivalent xpath filter
      LOGGER.trace("No replacement value found for {}", xpathExpression);
      modifiedFilter = filterBuilder.xpath(xpathExpression).is().like().fuzzyText(literal);
    }
    return modifiedFilter;
  }

  // Spatial filters
  @Override
  public Filter beyond(String propertyName, String wkt, double distance) {
    return filterBuilder.attribute(propertyName).is().beyond().wkt(wkt, distance);
  }

  @Override
  public Filter contains(String propertyName, String wkt) {
    return filterBuilder.attribute(propertyName).is().containing().wkt(wkt);
  }

  @Override
  public Filter dwithin(String propertyName, String wkt, double distance) {
    return filterBuilder.attribute(propertyName).is().withinBuffer().wkt(wkt, distance);
  }

  @Override
  public Filter intersects(String propertyName, String wkt) {
    return filterBuilder.attribute(propertyName).is().intersecting().wkt(wkt);
  }

  @Override
  public Filter within(String propertyName, String wkt) {
    return filterBuilder.attribute(propertyName).is().within().wkt(wkt);
  }

  @Override
  public Filter crosses(String propertyName, String wkt) {
    throw new UnsupportedOperationException(
        "crosses(String,String) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter disjoint(String propertyName, String wkt) {
    throw new UnsupportedOperationException(
        "disjoint(String,String) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter overlaps(String propertyName, String wkt) {
    // TODO: need to add support for this
    throw new UnsupportedOperationException(
        "overlaps(String,String) not supported by CopyFilterDelegate.");
  }

  @Override
  public Filter touches(String propertyName, String wkt) {
    throw new UnsupportedOperationException(
        "touches(String,String) not supported by CopyFilterDelegate.");
  }

  // Temporal filters
  @Override
  public Filter after(String propertyName, Date date) {
    return filterBuilder.attribute(propertyName).is().after().date(date);
  }

  @Override
  public Filter before(String propertyName, Date date) {
    return filterBuilder.attribute(propertyName).is().before().date(date);
  }

  @Override
  public Filter during(String propertyName, Date startDate, Date endDate) {
    return filterBuilder.attribute(propertyName).is().during().dates(startDate, endDate);
  }

  @Override
  public Filter relative(String propertyName, long duration) {
    return filterBuilder.attribute(propertyName).is().during().last(duration);
  }

  protected String findReplacement(String s) {
    String replacement = null;
    for (Map.Entry<Matcher, String> entry : replacementTable.entrySet()) {
      Matcher m = entry.getKey();
      m.reset(s);
      if (m.matches()) {
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace(
              "XPath query \"{}\" matched pattern \"{}\" - returning {}",
              s,
              m.pattern().toString(),
              s);
        }
        replacement = entry.getValue();
        break;
      } else {
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace(
              "XPath query \"{}\" did not match pattern \"{}\" - returning null",
              s,
              m.pattern().toString());
        }
      }
    }
    return replacement;
  }
}
