/*
 * SmartSprites Project
 *
 * Copyright (C) 2007-2009, Stanisław Osiński.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of  source code must  retain the above  copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following  disclaimer in  the documentation  and/or
 *   other materials provided with the distribution.
 *
 * - Neither the name of the SmartSprites Project nor the names of its contributors
 *   may  be used  to endorse  or  promote  products derived   from  this  software
 *   without specific prior written permission.
 *
 * - We kindly request that you include in the end-user documentation provided with
 *   the redistribution and/or in the software itself an acknowledgement equivalent
 *   to  the  following: "This product includes software developed by the SmartSprites
 *   Project."
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  AND
 * ANY EXPRESS OR  IMPLIED WARRANTIES, INCLUDING,  BUT NOT LIMITED  TO, THE IMPLIED
 * WARRANTIES  OF  MERCHANTABILITY  AND  FITNESS  FOR  A  PARTICULAR  PURPOSE   ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE  FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL,  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL  DAMAGES
 * (INCLUDING, BUT  NOT LIMITED  TO, PROCUREMENT  OF SUBSTITUTE  GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS;  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  ON
 * ANY  THEORY  OF  LIABILITY,  WHETHER  IN  CONTRACT,  STRICT  LIABILITY,  OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE)  ARISING IN ANY WAY  OUT OF THE USE  OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
(function($) {
  /**
   * Shows the actual UI elements. If there is no JavaScript enabled, this
   * function will not be called and the user will see the message shown
   * in <noscript> rather than the UI which requires JavaScript to run.
   */
  jQuery.fn.enableUi = function() {
    this.find(".disabled-ui").removeClass("disabled-ui");
  };

  /**
   * Dynamically adds markup required by the specific skin.
   */
  jQuery.enhanceMarkup = function() {
    // Extra wrappers for tabs
    $("#source-tabs li.tab").wrapInner('<div class="tab-left"><div class="tab-right"><div class="tab-inside"></div></div></div>');

    // A lead-in element for tabs
    $("#source-tabs").prepend('<div id="tab-lead-in"></div>');

    // Glows
    $("#query").glow("glow-small");
    $("#search").glow("glow-big");
  };

  /**
   * Adds the markup required to create a glow effect.
   */
  jQuery.fn.glow = function(glowClass) {
    this.each(function() {
      var $this = $(this);

      // Get content dimensions
      var contentWidth = $this.outerWidth();
      var contentHeight = $this.outerHeight();
    
      // Add glow markup
      var $glowDiv = $("<span class='" + glowClass + "'></span>");
      $this.before($glowDiv);
      $glowDiv.append("<span class='tl'></span><span class='t'></span><span class='tr'></span><span class='l'></span>");
      $glowDiv.append($this);
      $glowDiv.append("<span class='r'></span><span class='bl'></span><span class='b'></span><span class='br'></span>");

      // Extract border sizes
      var borderWidth = $glowDiv.find(".tl").width();
      var borderHeight = $glowDiv.find(".tl").height();

      // Add appropriate dimensions to the newly created markup
      $glowDiv.width(contentWidth + borderWidth * 2);
      $glowDiv.height(contentHeight + borderHeight * 2);
      $glowDiv.find(".t, .b").width(contentWidth);
      $glowDiv.find(".l, .r").height(contentHeight);
    });
  };
})(jQuery);
