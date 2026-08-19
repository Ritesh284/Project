/**
 * ==========================================================
 * CAR RENTAL - SEPARATE FULL-PAGE ADMIN PANEL (admin.js)
 * ==========================================================
 */

// Production Backend URL (Render): https://project-zf1j.onrender.com
const API_BASE = (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')
    ? (window.location.port === '8080' ? '' : 'http://localhost:8080')
    : 'https://project-zf1j.onrender.com';

console.log('[Car Rental Admin] Active API Base URL:', API_BASE);

let adminCars = [];
let adminBookings = [];
let adminUsers = [];

// ==========================================================
// 1. INITIALIZATION & AUTH GUARD
// ==========================================================
document.addEventListener('DOMContentLoaded', () => {
    checkAdminAuth();
});

function getAdminToken() {
    return localStorage.getItem('car_rental_token') || localStorage.getItem('token');
}

function getAdminUser() {
    const raw = localStorage.getItem('car_rental_user') || localStorage.getItem('user');
    try {
        return raw ? JSON.parse(raw) : null;
    } catch {
        return null;
    }
}

function getAuthHeaders(includeContentType = true) {
    const headers = {};
    if (includeContentType) {
        headers['Content-Type'] = 'application/json';
    }
    const token = getAdminToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

async function checkAdminAuth() {
    const token = getAdminToken();
    const user = getAdminUser();

    if (!token || !user || user.role !== 'ROLE_ADMIN') {
        alert('You are not authorized to access the Admin Panel. Redirecting to website.');
        window.location.href = '/';
        return;
    }

    // Verify token with backend
    try {
        const res = await fetch(`${API_BASE}/api/auth/me`, {
            headers: getAuthHeaders()
        });

        if (!res.ok) {
            throw new Error('Unauthorized');
        }

        const freshUser = await res.json();
        if (freshUser.role !== 'ROLE_ADMIN') {
            alert('You are not authorized to access the Admin Panel. Redirecting to website.');
            window.location.href = '/';
            return;
        }

        // Set user info on UI
        const name = freshUser.name || 'System Admin';
        const initials = name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() || 'AD';

        document.getElementById('admin-profile-name').textContent = name;
        document.getElementById('admin-avatar').textContent = initials;
        document.getElementById('header-admin-name').textContent = name;
        document.getElementById('header-admin-avatar').textContent = initials;

        // Load all data
        loadAdminStats();
        loadAdminRides();
        loadAdminCars();
        loadAdminUsers();

    } catch (e) {
        alert('Admin session expired or invalid. Please sign in as Admin.');
        window.location.href = '/';
    }
}

function handleAdminLogout() {
    localStorage.removeItem('car_rental_token');
    localStorage.removeItem('car_rental_user');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/';
}

// ==========================================================
// 2. SIDEBAR NAVIGATION & MOBILE TOGGLE
// ==========================================================
function toggleSidebar() {
    const sidebar = document.getElementById('admin-sidebar');
    const backdrop = document.getElementById('sidebar-backdrop');
    sidebar.classList.toggle('open');
    backdrop.classList.toggle('open');
}

function switchAdminSection(tabId, btn) {
    document.querySelectorAll('.sidebar-item').forEach(b => b.classList.remove('active'));
    if (btn) btn.classList.add('active');

    document.querySelectorAll('.admin-section').forEach(sec => sec.classList.remove('active'));
    const target = document.getElementById(tabId);
    if (target) target.classList.add('active');

    // Close mobile sidebar if open
    const sidebar = document.getElementById('admin-sidebar');
    const backdrop = document.getElementById('sidebar-backdrop');
    if (sidebar.classList.contains('open')) {
        sidebar.classList.remove('open');
        backdrop.classList.remove('open');
    }
}

// ==========================================================
// 3. DASHBOARD STATS
// ==========================================================
async function loadAdminStats() {
    try {
        const res = await fetch(`${API_BASE}/api/admin/dashboard/stats`, {
            headers: getAuthHeaders()
        });
        if (!res.ok) return;

        const s = await res.json();
        document.getElementById('stat-users').textContent = s.totalUsers || 0;
        document.getElementById('stat-cars').textContent = s.totalCars || 0;
        document.getElementById('stat-pending').textContent = s.pendingRides || 0;
        document.getElementById('stat-accepted').textContent = s.acceptedRides || 0;
        document.getElementById('stat-denied').textContent = s.deniedRides || 0;
        document.getElementById('stat-rev').textContent = `₹${formatNumber(s.totalRevenue || 0)}`;

        // Update badges in sidebar
        document.getElementById('badge-pending-rides').textContent = s.pendingRides || 0;
        document.getElementById('badge-total-cars').textContent = s.totalCars || 0;
    } catch (e) {
        console.error('Stats load error:', e);
    }
}

// ==========================================================
// 4. RIDE REQUESTS (TAB 1)
// ==========================================================
async function loadAdminRides() {
    const tbody = document.getElementById('admin-rides-tbody');
    try {
        const res = await fetch(`${API_BASE}/api/admin/bookings`, {
            headers: getAuthHeaders()
        });
        const rides = await res.json();
        adminBookings = rides || [];

        if (!rides || rides.length === 0) {
            tbody.innerHTML = `<tr><td colspan="10" class="text-center py-4 text-muted">No ride requests found.</td></tr>`;
            return;
        }

        tbody.innerHTML = rides.map(r => `
            <tr>
                <td><strong>#${r.id.substring(0, 8)}</strong></td>
                <td><strong>${escapeHtml(r.userName || 'Customer')}</strong></td>
                <td>${escapeHtml(r.userMobile || '')}<br><small style="color:#666;">${escapeHtml(r.userEmail || '')}</small></td>
                <td>${escapeHtml(r.carBrand || '')} ${escapeHtml(r.carName || '')}</td>
                <td>${escapeHtml(r.pickupCity || '')}<br><small style="color:#666;">${escapeHtml(r.pickupLocation || '')}</small></td>
                <td>${escapeHtml(r.dropCity || '')}<br><small style="color:#666;">${escapeHtml(r.dropLocation || '')}</small></td>
                <td>${r.pickupDate} → ${r.returnDate} (${r.totalDays || 1}d)</td>
                <td><strong>₹${formatNumber(r.totalAmount || 0)}</strong></td>
                <td><span class="badge badge-${(r.bookingStatus || 'pending').toLowerCase()}">${r.bookingStatus}</span></td>
                <td>
                    <div class="action-btns">
                        ${r.bookingStatus === 'PENDING' ? `
                            <button class="btn-sm btn-accept" onclick="acceptRide('${r.id}')"><i class='bx bx-check'></i> Accept</button>
                            <button class="btn-sm btn-deny-action" onclick="promptDenyRide('${r.id}')"><i class='bx bx-x'></i> Deny</button>
                        ` : ''}
                        ${r.bookingStatus === 'ACCEPTED' ? `
                            <button class="btn-sm btn-edit" onclick="completeRide('${r.id}')"><i class='bx bx-check-double'></i> Complete</button>
                        ` : ''}
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="10" style="color:red; text-align:center;">Failed to load ride requests.</td></tr>`;
    }
}

async function acceptRide(bookingId) {
    try {
        const res = await fetch(`${API_BASE}/api/admin/bookings/${bookingId}/accept`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to accept ride');

        showToast(`Ride #${bookingId.substring(0, 8)} accepted!`, 'success');
        loadAdminRides();
        loadAdminStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

function promptDenyRide(bookingId) {
    document.getElementById('deny-booking-id').value = bookingId;
    document.getElementById('deny-reason').value = '';
    document.getElementById('admin-deny-dialog').classList.remove('hidden');
}

function closeDenyDialog() {
    document.getElementById('admin-deny-dialog').classList.add('hidden');
}

async function submitDenial(e) {
    e.preventDefault();
    const bookingId = document.getElementById('deny-booking-id').value;
    const reason = document.getElementById('deny-reason').value;

    try {
        const res = await fetch(`${API_BASE}/api/admin/bookings/${bookingId}/deny`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify({ adminNote: reason })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to deny ride');

        closeDenyDialog();
        showToast(`Ride #${bookingId.substring(0, 8)} denied.`, 'error');
        loadAdminRides();
        loadAdminStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function completeRide(bookingId) {
    try {
        const res = await fetch(`${API_BASE}/api/admin/bookings/${bookingId}/complete`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to complete ride');

        showToast(`Ride #${bookingId.substring(0, 8)} completed!`, 'success');
        loadAdminRides();
        loadAdminStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ==========================================================
// 5. FLEET MANAGEMENT & ADD/EDIT CAR (TAB 2)
// ==========================================================
async function loadAdminCars() {
    const tbody = document.getElementById('admin-cars-tbody');
    try {
        const res = await fetch(`${API_BASE}/api/admin/cars`, {
            headers: getAuthHeaders()
        });
        const cars = await res.json();
        adminCars = cars || [];

        if (!cars || cars.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4 text-muted">No cars in fleet. Click "+ Add New Car" to create one.</td></tr>`;
            return;
        }

        tbody.innerHTML = cars.map(c => `
            <tr>
                <td><strong>#${c.id.substring(0, 8)}</strong></td>
                <td><img src="${getCarImageSrc(c.image)}" class="table-car-img" alt="${escapeHtml(c.carName)}"></td>
                <td><strong>${escapeHtml(c.brand)}</strong> ${escapeHtml(c.carName)}<br><small style="color:#666;">Model: ${escapeHtml(c.model || '')}</small></td>
                <td><span class="badge badge-completed">${escapeHtml(c.category || 'General')}</span></td>
                <td><strong>₹${formatNumber(c.pricePerDay || 0)}</strong></td>
                <td><small>${escapeHtml(c.fuelType || '')} • ${escapeHtml(c.transmission || '')} • ${c.seats || 5} Seats</small></td>
                <td>
                    <span class="badge ${c.available ? 'badge-available' : 'badge-unavailable'}" style="cursor:pointer;" onclick="toggleCarAvailability('${c.id}')" title="Click to toggle">
                        ${c.available ? 'Available' : 'Unavailable'}
                    </span>
                </td>
                <td>
                    <div class="action-btns">
                        <button class="btn-sm btn-edit" onclick="editCar('${c.id}')"><i class='bx bx-edit'></i> Edit</button>
                        <button class="btn-sm btn-delete" onclick="deleteCar('${c.id}')"><i class='bx bx-trash'></i> Delete</button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="8" style="color:red; text-align:center;">Failed to load fleet cars.</td></tr>`;
    }
}

function openAddCarForm() {
    document.getElementById('admin-car-id').value = '';
    document.getElementById('admin-car-form').reset();
    document.getElementById('car-form-title').textContent = 'Add Car to Fleet';
    document.getElementById('admin-car-avail').checked = true;
    document.getElementById('admin-car-form-wrapper').style.display = 'block';
    document.getElementById('admin-car-brand').focus();
}

function closeAddCarForm() {
    document.getElementById('admin-car-form-wrapper').style.display = 'none';
}

function editCar(carId) {
    const car = adminCars.find(c => c.id === carId);
    if (!car) return;

    document.getElementById('admin-car-id').value = car.id;
    document.getElementById('admin-car-brand').value = car.brand || '';
    document.getElementById('admin-car-name').value = car.carName || '';
    document.getElementById('admin-car-model').value = car.model || '';
    document.getElementById('admin-car-category').value = car.category || 'SUV';
    document.getElementById('admin-car-price').value = car.pricePerDay || '';
    document.getElementById('admin-car-fuel').value = car.fuelType || 'Petrol';
    document.getElementById('admin-car-trans').value = car.transmission || 'Automatic';
    document.getElementById('admin-car-seats').value = car.seats || 5;
    document.getElementById('admin-car-image').value = car.image || '';
    document.getElementById('admin-car-desc').value = car.description || '';
    document.getElementById('admin-car-avail').checked = car.available;

    document.getElementById('car-form-title').textContent = 'Edit Fleet Car';
    document.getElementById('admin-car-form-wrapper').style.display = 'block';
    window.scrollTo({ top: document.getElementById('admin-car-form-wrapper').offsetTop - 80, behavior: 'smooth' });
}

async function handleImageUpload(e) {
    const file = e.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
        const res = await fetch(`${API_BASE}/api/admin/cars/upload-image`, {
            method: 'POST',
            headers: getAuthHeaders(false),
            body: formData
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Image upload failed');
        const imgPath = data.imageUrl || (data.fileName ? `/uploads/${data.fileName}` : '');
        if (imgPath) {
            document.getElementById('admin-car-image').value = imgPath;
            showToast('Image uploaded successfully!', 'success');
        }
    } catch (err) {
        showToast('Image upload failed', 'error');
    }
}

async function handleSaveCar(e) {
    e.preventDefault();

    const carId = document.getElementById('admin-car-id').value;
    const carData = {
        brand: document.getElementById('admin-car-brand').value.trim(),
        carName: document.getElementById('admin-car-name').value.trim(),
        model: document.getElementById('admin-car-model').value.trim(),
        category: document.getElementById('admin-car-category').value,
        pricePerDay: parseFloat(document.getElementById('admin-car-price').value),
        fuelType: document.getElementById('admin-car-fuel').value,
        transmission: document.getElementById('admin-car-trans').value,
        seats: parseInt(document.getElementById('admin-car-seats').value),
        image: document.getElementById('admin-car-image').value.trim() || 'BMW.jpeg',
        description: document.getElementById('admin-car-desc').value.trim(),
        available: document.getElementById('admin-car-avail').checked
    };

    try {
        const url = carId ? `${API_BASE}/api/admin/cars/${carId}` : `${API_BASE}/api/admin/cars`;
        const method = carId ? 'PUT' : 'POST';

        const res = await fetch(url, {
            method,
            headers: getAuthHeaders(),
            body: JSON.stringify(carData)
        });

        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to save car');

        showToast(carId ? 'Car updated successfully!' : 'New car added to fleet!', 'success');
        closeAddCarForm();
        loadAdminCars();
        loadAdminStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function toggleCarAvailability(carId) {
    try {
        const res = await fetch(`${API_BASE}/api/admin/cars/${carId}/toggle-availability`, {
            method: 'PATCH',
            headers: getAuthHeaders()
        });
        if (!res.ok) throw new Error('Failed to update availability');

        showToast('Car availability updated', 'success');
        loadAdminCars();
        loadAdminStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function deleteCar(carId) {
    if (!confirm('Are you sure you want to remove this car from the fleet?')) return;

    try {
        const res = await fetch(`${API_BASE}/api/admin/cars/${carId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        if (!res.ok) throw new Error('Failed to delete car');

        showToast('Car removed from fleet', 'success');
        loadAdminCars();
        loadAdminStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ==========================================================
// 6. REGISTERED USERS (TAB 3)
// ==========================================================
async function loadAdminUsers() {
    const tbody = document.getElementById('admin-users-tbody');
    try {
        const res = await fetch(`${API_BASE}/api/admin/users`, {
            headers: getAuthHeaders()
        });
        const users = await res.json();
        adminUsers = users || [];

        if (!users || users.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No users found.</td></tr>`;
            return;
        }

        tbody.innerHTML = users.map(u => `
            <tr>
                <td><strong>#${u.id.substring(0, 8)}</strong></td>
                <td><strong>${escapeHtml(u.name || '')}</strong></td>
                <td>${escapeHtml(u.email || '')}</td>
                <td>${escapeHtml(u.mobileNumber || 'N/A')}</td>
                <td><span class="badge ${u.role === 'ROLE_ADMIN' ? 'badge-pending' : 'badge-completed'}">${u.role}</span></td>
                <td><small>${formatDate(u.createdAt)}</small></td>
            </tr>
        `).join('');
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="6" style="color:red; text-align:center;">Failed to load users.</td></tr>`;
    }
}

// ==========================================================
// 7. UTILITIES & TOASTS
// ==========================================================
function getCarImageSrc(img) {
    if (!img) return 'BMW.jpeg';
    if (img.startsWith('http')) return img;
    if (img.startsWith('/uploads/')) return `${API_BASE}${img}`;
    if (img.startsWith('uploads/')) return `${API_BASE}/${img}`;
    return img;
}

function formatNumber(num) {
    if (!num) return '0';
    return Number(num).toLocaleString('en-IN');
}

function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    try {
        const d = new Date(dateStr);
        return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
    } catch {
        return dateStr;
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function showToast(msg, type = 'info') {
    const container = document.getElementById('admin-toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast-${type === 'error' ? 'error' : 'success'}`;
    toast.textContent = msg;

    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3500);
}
