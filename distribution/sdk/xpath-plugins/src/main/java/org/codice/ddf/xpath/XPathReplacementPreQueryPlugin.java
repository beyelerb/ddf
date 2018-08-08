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

import ddf.catalog.filter.FilterAdapter;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.FilterDelegate;
import ddf.catalog.operation.Query;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.impl.QueryImpl;
import ddf.catalog.operation.impl.QueryRequestImpl;
import ddf.catalog.plugin.PluginExecutionException;
import ddf.catalog.plugin.PreQueryPlugin;
import ddf.catalog.plugin.StopProcessingException;
import ddf.catalog.source.UnsupportedQueryException;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.lang.StringUtils;
import org.opengis.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ************************************************************************************* Follow DDF
 * Developer's Guide to implement Life-cycle Services, Sources, or Transformers This
 * template/example shows the skeleton code for a Pre-Query Service
 * **************************************************************************************
 */
public class XPathReplacementPreQueryPlugin implements PreQueryPlugin {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(XPathReplacementPreQueryPlugin.class);

  private static final String ENTERING = "ENTERING {}";

  private static final String EXITING = "EXITING {}";

  private FilterAdapter filterAdapter;

  private FilterBuilder filterBuilder;

  private static final String REPLACEMENT_STRING_FORMAT = "\"(.*)\":\"(.*)\"$";

  protected static final Pattern REPLACEMENT_PATTERN = Pattern.compile(REPLACEMENT_STRING_FORMAT);

  protected static final Matcher REPLACEMENT_MATCHER = REPLACEMENT_PATTERN.matcher("dummy");

  private HashMap<Matcher, String> replacementTable = new HashMap<>();

  public XPathReplacementPreQueryPlugin(FilterAdapter filterAdapter, FilterBuilder filterBuilder) {
    LOGGER.trace("INSIDE: XPathReplacementPreQueryPlugin constructor");
    this.filterAdapter = filterAdapter;
    this.filterBuilder = filterBuilder;
  }

  @Override
  public QueryRequest process(QueryRequest input)
      throws PluginExecutionException, StopProcessingException {
    String methodName = "process";
    LOGGER.trace(ENTERING, methodName);

    QueryRequest newQueryRequest = input;

    if (input != null) {
      Query query = input.getQuery();

      if (query != null) {
        FilterDelegate<Filter> delegate = new XpathFilterDelegate(filterBuilder, replacementTable);
        try {
          // Make a defensive copy of the original filter (just in case anyone else
          // expects
          // it to remain unmodified)
          Filter modifiedFilter = filterAdapter.adapt(query, delegate);

          // Define the extra query clause(s) to add to the copied filter
          // This will create a filter with a search phrase of:
          // ((("schematypesearch") and ("test" and ("ISAF" or "CAN"))))
          /*          Filter contextualFilter = filterBuilder.attribute(Metacard.ANY_TEXT).like().text("test");
                    Filter releasableToFilter1 =
                        filterBuilder.attribute(Metacard.ANY_TEXT).like().text("ISAF");
                    Filter releasableToFilter2 =
                        filterBuilder.attribute(Metacard.ANY_TEXT).like().text("CAN");
                    Filter orFilter = filterBuilder.anyOf(releasableToFilter1, releasableToFilter2);
                    Filter extraFilter = filterBuilder.allOf(contextualFilter, orFilter);

                    // AND this PreQueryPlugin's extra query clause(s) to the copied filter
                    Filter modifiedFilter = filterBuilder.allOf(copiedFilter, extraFilter);
          */
          // Create a new QueryRequest using the modified filter and the attributes from
          // the original query
          LOGGER.trace("Filter after delegate: {}", modifiedFilter);
          QueryImpl newQuery =
              new QueryImpl(
                  modifiedFilter,
                  query.getStartIndex(),
                  query.getPageSize(),
                  query.getSortBy(),
                  query.requestsTotalResultsCount(),
                  query.getTimeoutMillis());
          newQueryRequest =
              new QueryRequestImpl(
                  newQuery, input.isEnterprise(), input.getSourceIds(), input.getProperties());
        } catch (UnsupportedQueryException e) {
          throw new PluginExecutionException(e);
        }
      }
    }

    LOGGER.trace(EXITING, methodName);

    return newQueryRequest;
  }

  public void setXpathReplacements(Set<String> replacements) {
    if (replacements != null) {
      replacementTable.clear();
      for (String enteredString : replacements) {
        addReplacementRule(enteredString);
      }
    }
  }

  /**
   * Adds an individual rule (expressed in String form) The String form of the rule is a two-part
   * string with each part surrounded by quotes and separated by a colon (:) with no spaces<br>
   * "&lt;xpath regex&gt;":"&lt;replacement string&gt;"
   *
   * @param ruleStr the rule to be added (in String form)
   */
  private void addReplacementRule(String ruleStr) {
    if (StringUtils.isEmpty(ruleStr)) {
      return;
    }

    REPLACEMENT_MATCHER.reset(ruleStr);
    if (REPLACEMENT_MATCHER.matches()) {
      try {
        String regex = REPLACEMENT_MATCHER.group(1);
        String replacement = REPLACEMENT_MATCHER.group(2);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher =
            pattern.matcher("dummy"); // will be reset when processing xpath expressions
        LOGGER.debug(
            "Adding XPath replacement rule: {} and replacement string: {}", regex, replacement);
        replacementTable.put(matcher, replacement);
      } catch (PatternSyntaxException | IllegalStateException | IndexOutOfBoundsException ex) {
        LOGGER.warn("Error processing xpath rule - ignoring: ", ex);
      }
    } else {
      LOGGER.warn("Ignoring XPath replacement rule doesn't match expected format: {}", ruleStr);
    }
  }
}
