$(document).ready(function() {
              $("#stepsTable").on('click', '.add', function() {
//                $('#stepsTable td:nth-child(2)').hide();
//                $('#stepsTable td:nth-child(3)').hide();
                rowObj = $("#stepsTable tbody tr:first").clone();
                $("#stepsTable").append(rowObj);
              });
              $("#stepsTable").on('click', '.remove', function() {
                if ($('#stepsTable tbody tr').length > 1) {
//                  $('#stepsTable td:nth-child(2)').hide();
//                  $('#stepsTable td:nth-child(3)').hide();
                  $(this).parent().parent().remove();
                }
              });
              hideEmptyCols($("#stepsTable"));
            });
            hideEmptyCols($("#stepsTable"));

            function hideEmptyCols(table) {
              var rows = $("tr", table).length - 1;
              var numCols = $("th", table).length;
              for (var i = 1; i <= numCols; i++) {
                if ($("span:empty", $("td:nth-child(" + i + ")", table)).length == rows) {
                  $("td:nth-child(" + i + ")", table).hide(); //hide <td>'s
                  $("th:nth-child(" + i + ")", table).hide(); //hide header <th>
                }
              }
            }

            const txHeight = 16;
            const tx = document.getElementsByTagName("textarea");
            for (let i = 0; i < tx.length; i++) {
              if (tx[i].value == '') {
                tx[i].setAttribute("style", "height:" + txHeight + "px;overflow-y:hidden;");
              } else {
                tx[i].setAttribute("style", "height:" + (tx[i].scrollHeight) + "px;overflow-y:hidden;");
              }
              tx[i].addEventListener("input", OnInput, false);
            }

            function OnInput(e) {
              this.style.height = "auto";
              this.style.height = (this.scrollHeight) + "px";
            }