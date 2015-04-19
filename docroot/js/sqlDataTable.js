function SQLDataTable(tableId, options, extensions) {

   if (extensions != null) {
      if (extensions.indexOf("FixedColumns") > -1) {
         if (options == null) {
            options = {
               scrollX : true
            };
         } else {
            if (!(options.scrollX)) {
               options.scrollX = true;
            }
         }
      }
   }

   var table = null;
   if (options == null) {
      table = $("#" + tableId).DataTable();
   } else {
      table = $("#" + tableId).DataTable(options);
   }

   if (extensions != null) {
      if ((extensions.indexOf("FixedColumns") > -1) && (options != null) && (options.scrollX)) {
         new $.fn.dataTable.FixedColumns(table);
      }

      if (extensions.indexOf("ColReorder") > -1) {
         new $.fn.dataTable.ColReorder(table);
      }

      if (extensions.indexOf("HighlightingColumns") > -1) {

         $("#" + tableId + " tbody").on('mouseover', 'td', function() {
            $(table.cells().nodes()).removeClass('sql-datatable-highlight');
            if ((table) && (table != null) && (table.cell)) {
               var cell = table.cell(this);
               if ((cell != null) && (cell.index)) {
                  var index = table.cell(this).index();
                  if ((index != null) && (index.column)) {

                     var column = table.cell(this).index().column;
                     if (column != null) {
                        $(table.column(column).nodes()).addClass('sql-datatable-highlight');
                     }
                  } else {
                     $(table.column(0).nodes()).addClass('sql-datatable-highlight');
                  }
               }
            }

         }).on('mouseleave', function() {
            $(table.cells().nodes()).removeClass('sql-datatable-highlight');
         });
      }
   }

   return (table);
}