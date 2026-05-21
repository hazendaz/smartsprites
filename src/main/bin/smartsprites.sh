#!/bin/sh
#
# SPDX-License-Identifier: BSD-3-Clause
# See LICENSE file for details.
#
# Copyright 2021-2026 Hazendaz
# Copyright (C) 2007-2009, Stanisław Osiński.
#


#
# Add extra JVM options here
#
OPTS="-Xms64m -Xmx256m"

java $OPTS -D"java.ext.dirs=` dirname "$0" | sed 's/ /\\ /g' `/lib" org.carrot2.labs.smartsprites.SmartSprites "$@"
