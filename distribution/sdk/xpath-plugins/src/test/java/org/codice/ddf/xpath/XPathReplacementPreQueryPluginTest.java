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

import static org.junit.Assert.assertTrue;

import ddf.catalog.filter.FilterAdapter;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.adapter.GeotoolsFilterAdapterImpl;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.impl.QueryImpl;
import ddf.catalog.operation.impl.QueryRequestImpl;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;

public class XPathReplacementPreQueryPluginTest {

  Set<String> replacementStrings = new HashSet<>();
  FilterBuilder builder = new GeotoolsFilterBuilder();
  FilterAdapter fa = new GeotoolsFilterAdapterImpl();
  XPathReplacementPreQueryPlugin plugin = new XPathReplacementPreQueryPlugin(fa, builder);
  Filter originalFilter;
  String xpathExpression = "/ddms:Resource/ddms:security/@ICSM:releasableTo";
  QueryImpl newQuery;
  QueryRequest originalQueryRequest;

  @Before
  public void setupTest() {
    replacementStrings.add("\"" + xpathExpression + "\":\"security.releasableTo\"");
    plugin.setXpathReplacements(replacementStrings);
    // the original query
    originalFilter = builder.xpath(xpathExpression).is().like().text("FVEY");
    newQuery = new QueryImpl(originalFilter, 0, 30, SortBy.NATURAL_ORDER, true, 3000);
    originalQueryRequest =
        new QueryRequestImpl(newQuery, true, null, new HashMap<String, Serializable>());
  }

  @Test
  public void setXpathReplacements() {
    plugin.setXpathReplacements(replacementStrings);
  }

  @Test
  public void processTest() throws Exception {
    QueryRequest qr = plugin.process(originalQueryRequest);
    String filter = qr.getQuery().toString();
    assertTrue(filter.contains("[ security.releasableTo is like FVEY ]"));
  }
}
