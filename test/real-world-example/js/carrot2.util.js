/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
jQuery.delegate = function(rules) {
  return function(e) {
    var target = jQuery(e.target);
    for (var selector in rules) {
      if (target.is(selector)) {
        return rules[selector].apply(this, jQuery.makeArray(arguments));
      }
    }
  }
};
