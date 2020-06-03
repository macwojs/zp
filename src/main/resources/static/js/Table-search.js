$(document).ready( function () {
    $('.sorted-table').DataTable({
        "language": {
            lengthMenu: "Wyświetl _MENU_ pozycji",
            zeroRecords: "Nic nie znaleziono",
            search: "Wyszukaj",
            info: "Strona _PAGE_ z _PAGES_",
            infoEmpty: "Brak danych do wyświetlenia",
            loadingRecords: "Wczytywanie",
            paginate: {
                first: "Pierwsza",
                previous: "Poprzednia",
                next: "następna",
                last: "ostatnia"
            },
            infoFiltered: "(wybrano z pośród _MAX_ rekordów)"
        }
    });
} );