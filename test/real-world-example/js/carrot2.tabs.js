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
   * Adds dynamic behaviour to tabs, including click response and sorting by
   * drag&drop.
   */
  jQuery.fn.sourceTabs = function() {
    this.each(function() {
      $tabContainer = $(this);

      // Make the tabs sortable
      $tabContainer.find("ul").sortable({
        change: function() {
          $tabContainer.trigger("tabsChanged");
        },
        sort: function() {  
          if (!window.tabDragStarted) {
            $tabContainer.find("li:last").addClass("drag");
            $tabContainer.find("li:not(.drag), #tab-lead-in").css("visibility", "visible").animate({opacity: 0.5}, 300);
            window.tabDragStarted = true;
          }
        },
        stop: function() {
          window.tabDragStarted = false;
          $tabContainer.find("li:not(.drag), #tab-lead-in").css("visibility", "visible").animate({opacity: 1.0}, 300);
        },
        start: function(e) {
          activateTab(e, $tabContainer);
        },
        revert: true,
        distance: 15
      });

      // Make tabs respond to clicks
      $tabContainer.click($.delegate({
        ".label": function(e) {
          activateTab(e, $tabContainer);
          return false;
        }
      }));

      // Bind listener to tab structure change events
      $tabContainer.bind("tabsChanged", updateTabs);
      $tabContainer.find("a").bind("tabActivated", copyTabInfo);
    });
  };

  /**
   * Activates provided tab.
   */
  activateTab = function(e, $tabContainer) {
    $tabContainer.find("li.tab").removeClass("active").addClass("passive");
    $(e.target).parents("li.tab").removeClass("passive").addClass("active");
    $tabContainer.trigger("tabsChanged");
    $(e.target).trigger("tabActivated");
  };

  /**
   * Updates the look of tabs after the active tab or tab order has changed.
   */
  updateTabs = function(e)
  {
    var $tabContainer = $(e.target);
    var $tabs = $tabContainer.find("li:visible:not(.drag)");
    $tabs.removeClass("passive-first active-first passive-last active-last before-active");

    $.each($tabs, function(i, tab) {
      $tab = $(tab);
      var status = tabStatus(tab);
      var nextStatus = tabStatus($tabs[i + 1]);
      var orderSuffix = "";
      if (i == 0) {
        orderSuffix = "-first";
        if (status == "active") {
          $tabContainer.addClass("first-active");
        }
        else {
          $tabContainer.removeClass("first-active");
        }
      } else if (i == $tabs.length - 1) {
        orderSuffix = "-last";
      }

      $tab.addClass(status + orderSuffix);
      if (nextStatus == "active") {
        $tab.addClass("before-active");
      }
    });
  };

  /**
   * Returns the status ("active" or "passive") of the provided tab.
   */
  tabStatus = function(tabElement) {
    if (!tabElement) {
      return null;
    }
    return (tabElement && tabElement.className.indexOf("passive") >= 0 ? "passive" : "active");
  };

  copyTabInfo = function(e) {
    $("#tab-info").html($(e.target).siblings("span.tab-info").clone().removeClass("hide"));
  }
})(jQuery);
