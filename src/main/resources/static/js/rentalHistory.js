function fetchRentalHistoryData(page, size) {
    let apiUrl = getApiUrl(page, size);
    $.ajax({
        url: apiUrl,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            const tableBody = $('#rentalHistoryTableBody');
            tableBody.empty();

            data.content.forEach(rental => {
                const row = $('<tr></tr>');

                const customerNameCell = $('<td></td>');
                const customerLink = $('<a></a>')
                    .attr('href', `/hosting/rental-history/detail/${rental.rentalId}`)
                    .text(rental.user.fullname)
                    .addClass('btn btn-info btn-sm');
                customerNameCell.append(customerLink);
                row.append(customerNameCell);
                row.append($('<td></td>').text(rental.house.propertyName));
                row.append($('<td></td>').text(new Date(rental.startDate).toLocaleString()));
                row.append($('<td></td>').text(new Date(rental.endDate).toLocaleString()));
                row.append($('<td></td>').text(rental.totalCost));

                const statusCell = $('<td></td>');
                const statusSpan = $('<span></span>');
                switch (rental.status) {
                    case 'Pending':
                        statusSpan.text('Chờ xử lý').addClass('status pending');
                        break;
                    case 'Checked_in':
                        statusSpan.text('Đã nhận phòng').addClass('status checked-in');
                        break;
                    case 'Checked_out':
                        statusSpan.text('Đã trả phòng').addClass('status checked-out');
                        break;
                    case 'Cancelled':
                        statusSpan.text('Đã hủy').addClass('status cancelled');
                        break;
                    default:
                        statusSpan.text('Không xác định').addClass('status unknown');
                        break;
                }
                statusCell.append(statusSpan);
                row.append(statusCell);

                const actionCell = $('<td></td>');
                if (rental.status === 'Pending') {
                    actionCell.append($('<a></a>')
                        .attr('href', `/hosting/rental-history/check-in/${rental.rentalId}`)
                        .text('Check-in')
                        .addClass('action-btn check-in-btn'));
                } else if (rental.status === 'Checked_in') {
                    actionCell.append($('<a></a>')
                        .attr('href', `/hosting/rental-history/check-out/${rental.rentalId}`)
                        .text('Check-out')
                        .addClass('action-btn check-out-btn'));
                }
                row.append(actionCell);

                tableBody.append(row);
            });
            separatePages(data, size);
        },
        error: function (error) {
            console.error('Error fetching rental history data:', error);
        }
    });
}
function separatePages(data, size) {
    const pagination = $('#pagination');
    pagination.empty();

    const prevPageItem = $('<li></li>').addClass('page-item');
    if (data.pageable.pageNumber > 0) {
        const prevPageLink = $('<a></a>')
            .addClass('page-link')
            .attr('href', '#')
            .text('Về trước')
            .click(function (e) {
                e.preventDefault();
                fetchRentalHistoryData(data.pageable.pageNumber - 1, size);
            });
        prevPageItem.append(prevPageLink);
    } else {
        prevPageItem.addClass('disabled');
        prevPageItem.append($('<span></span>').addClass('page-link').text('Về trước'));
    }
    pagination.append(prevPageItem);

    for (let i = 0; i < data.totalPages; i++) {
        const pageItem = $('<li></li>').addClass('page-item');
        if (i === data.pageable.pageNumber) {
            pageItem.addClass('active');
        }
        const pageLink = $('<a></a>')
            .addClass('page-link')
            .attr('href', '#')
            .text(i + 1)
            .click(function (e) {
                e.preventDefault();
                fetchRentalHistoryData(i, size);
            });
        pageItem.append(pageLink);
        pagination.append(pageItem);
    }

    const nextPageItem = $('<li></li>').addClass('page-item');
    if (data.pageable.pageNumber < data.totalPages - 1) {
        const nextPageLink = $('<a></a>')
            .addClass('page-link')
            .attr('href', '#')
            .text('Về sau')
            .click(function (e) {
                e.preventDefault();
                fetchRentalHistoryData(data.pageable.pageNumber + 1, size);
            });
        nextPageItem.append(nextPageLink);
    } else {
        nextPageItem.addClass('disabled');
        nextPageItem.append($('<span></span>').addClass('page-link').text('Về sau'));
    }
    pagination.append(nextPageItem);
}
function getApiUrl(page, size) {
    const propertyName = $('#propertyName').val().trim();
    const startDate = $('#startDate').val();
    const endDate = $('#endDate').val();
    const status = $('#statusFilter').val();

    if (propertyName || startDate || endDate || status) {
        let searchUrl = `http://localhost:8080/hosting/rental-history/api/search?page=${page}&size=${size}`;
        if (propertyName) searchUrl += `&propertyName=${encodeURIComponent(propertyName)}`;
        if (startDate) searchUrl += `&startDate=${encodeURIComponent(startDate)}`;
        if (endDate) searchUrl += `&endDate=${encodeURIComponent(endDate)}`;
        if (status) searchUrl += `&status=${encodeURIComponent(status)}`;
        return searchUrl;
    }

    return `http://localhost:8080/hosting/rental-history/api/data?page=${page}&size=${size}`;
}
$(document).ready(function () {
    const pageSize = 5;
    fetchRentalHistoryData(0, pageSize);
    $('#propertyName, #startDate, #endDate, #statusFilter').on('input change', function () {
        fetchRentalHistoryData(0, pageSize);
    });
});
