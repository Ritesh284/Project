/**
 * Car Rental Full-Stack Application Logic
 * Preserves 100% of original UI while connecting to Spring Boot REST APIs
 */

// Production Backend URL (Render): https://project-zf1j.onrender.com
const API_BASE = (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')
    ? (window.location.port === '8080' ? '' : 'http://localhost:8080')
    : 'https://project-zf1j.onrender.com';

// Global state
let currentCars = [];
let allStates = [];
let selectedCarForBooking = null;

// ========================================================
// 1. INITIALIZATION & UI INTERACTIVITY
// ========================================================
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initScrollReveal();
    syncAuthHeader();
    loadCars();
    loadStates();
    initHeroDates();
});

function initNavigation() {
    const menu = document.querySelector('#menu-icon');
    const navbar = document.querySelector('.navbar');

    if (menu && navbar) {
        menu.onclick = () => {
            menu.classList.toggle('bx-x');
            navbar.classList.toggle('active');
        };

        window.onscroll = () => {
            menu.classList.remove('bx-x');
            navbar.classList.remove('active');
        };
    }

    // Attach header auth button click listeners
    const signupBtn = document.getElementById('header-signup-btn');
    const signinBtn = document.getElementById('header-signin-btn');
    if (signupBtn) signupBtn.onclick = () => openModal('signup-modal');
    if (signinBtn) signinBtn.onclick = () => openModal('signin-modal');
}

function initScrollReveal() {
    if (typeof ScrollReveal !== 'undefined') {
        const sr = ScrollReveal({
            distance: '60px',
            duration: 2000,
            delay: 300,
            reset: false
        });

        sr.reveal('.text', { delay: 200, origin: 'top' });
        sr.reveal('.form-container form', { delay: 400, origin: 'left' });
        sr.reveal('.heading', { delay: 400, origin: 'top' });
        sr.reveal('.ride-container .box', { delay: 400, origin: 'top', interval: 100 });
        sr.reveal('.about-container, .about.container', { delay: 400, origin: 'top' });
        sr.reveal('.reviews-container .box', { delay: 400, origin: 'top', interval: 100 });
        sr.reveal('.newsletter', { delay: 300, origin: 'bottom' });
    }
}

function initHeroDates() {
    const today = new Date().toISOString().split('T')[0];
    const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];
    
    const heroPickup = document.getElementById('hero-pickup-date');
    const heroReturn = document.getElementById('hero-return-date');
    if (heroPickup) {
        heroPickup.min = today;
        heroPickup.value = today;
    }
    if (heroReturn) {
        heroReturn.min = today;
        heroReturn.value = tomorrow;
    }
}

// ========================================================
// 2. AUTHENTICATION & HEADER STATE
// ========================================================
function getToken() {
    return localStorage.getItem('car_rental_token');
}

function getCurrentUser() {
    const raw = localStorage.getItem('car_rental_user');
    try {
        return raw ? JSON.parse(raw) : null;
    } catch (e) {
        return null;
    }
}

function setAuth(token, user) {
    localStorage.setItem('car_rental_token', token);
    localStorage.setItem('car_rental_user', JSON.stringify(user));
    syncAuthHeader();
}

function clearAuth() {
    localStorage.removeItem('car_rental_token');
    localStorage.removeItem('car_rental_user');
    syncAuthHeader();
}

function getAuthHeaders(includeContentType = true) {
    const headers = {};
    if (includeContentType) {
        headers['Content-Type'] = 'application/json';
    }
    const token = getToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

function syncAuthHeader() {
    const container = document.getElementById('header-auth-buttons');
    if (!container) return;

    const user = getCurrentUser();

    if (!user) {
        container.innerHTML = `
            <a href="javascript:void(0)" class="sign-up" onclick="openModal('signup-modal')">Sign Up</a>
            <a href="javascript:void(0)" class="sign-in" onclick="openModal('signin-modal')">Sign In</a>
        `;
    } else {
        const isAdmin = user.role === 'ROLE_ADMIN';
        container.innerHTML = `
            ${isAdmin ? `<a href="javascript:void(0)" class="admin-link" onclick="openAdminPanel()"><i class='bx bxs-dashboard'></i> Admin Panel</a>` : ''}
            <a href="javascript:void(0)" class="sign-up" onclick="openMyBookings()"><i class='bx bxs-car'></i> My Bookings</a>
            <div class="user-badge"><i class='bx bxs-user-check'></i> ${user.name.split(' ')[0]}</div>
            <a href="javascript:void(0)" class="sign-in" onclick="handleLogout()">Logout</a>
        `;
    }
}

async function handleLogin(e) {
    e.preventDefault();
    const identifier = document.getElementById('login-identifier').value.trim();
    const password = document.getElementById('login-password').value;

    try {
        const res = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ identifier, password })
        });

        const data = await res.json();
        if (!res.ok) {
            throw new Error(data.message || 'Login failed');
        }

        setAuth(data.token, {
            id: data.id,
            name: data.name,
            email: data.email,
            mobileNumber: data.mobileNumber,
            role: data.role
        });

        closeModal('signin-modal');
        document.getElementById('signin-form').reset();
        showToast(data.message || `Welcome back, ${data.name}!`, 'success');

        if (data.role === 'ROLE_ADMIN') {
            openAdminPanel();
        }
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const name = document.getElementById('reg-name').value.trim();
    const mobileNumber = document.getElementById('reg-mobile').value.trim();
    const email = document.getElementById('reg-email').value.trim();
    const password = document.getElementById('reg-password').value;
    const confirmPassword = document.getElementById('reg-confirm-password').value;

    if (password !== confirmPassword) {
        showToast("Passwords do not match!", 'error');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/api/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, mobileNumber, email, password, confirmPassword })
        });

        const data = await res.json();
        if (!res.ok) {
            throw new Error(data.message || 'Registration failed');
        }

        setAuth(data.token, {
            id: data.id,
            name: data.name,
            email: data.email,
            mobileNumber: data.mobileNumber,
            role: data.role
        });

        closeModal('signup-modal');
        document.getElementById('signup-form').reset();
        showToast('Registration successful! Welcome to Car Rental.', 'success');
    } catch (err) {
        showToast(err.message, 'error');
    }
}

function handleLogout() {
    clearAuth();
    showToast('Logged out successfully', 'info');
}

// ========================================================
// 3. DYNAMIC CAR LISTING (GET /api/cars)
// ========================================================
async function loadCars(searchQuery = '') {
    const container = document.getElementById('services-container');
    if (!container) return;

    try {
        let url = `${API_BASE}/api/cars`;
        if (searchQuery) {
            url += `?search=${encodeURIComponent(searchQuery)}`;
        }

        const res = await fetch(url);
        if (!res.ok) {
            throw new Error('Failed to load cars from backend');
        }

        let cars = await res.json();
        if (!cars || cars.length === 0) {
            const fallbackRes = await fetch(`${API_BASE}/api/cars`);
            if (fallbackRes.ok) {
                cars = await fallbackRes.json();
            }
        }
        currentCars = cars || [];
        renderCarCards(currentCars);
    } catch (err) {
        console.error('Error fetching cars:', err);
    }
}

function renderCarCards(cars) {
    const container = document.getElementById('services-container');
    if (!container) return;

    if (!cars || cars.length === 0) {
        if (currentCars && currentCars.length > 0) {
            cars = currentCars;
        } else {
            return;
        }
    }

    container.innerHTML = cars.map(car => {
        const monthlyRate = Math.round((car.pricePerDay || 1500) * 30);
        const imgPath = (car.image && (car.image.startsWith('/uploads/') || car.image.startsWith('uploads/'))) 
            ? (car.image.startsWith('/') ? `${API_BASE}${car.image}` : `${API_BASE}/${car.image}`) 
            : (car.image || 'Defender1.jpeg');

        return `
            <div class="box" data-car-id="${car.id}">
              <div class="box-img">
                <img src="${imgPath}" alt="${car.carName}" onerror="this.onerror=null; this.src='Defender1.jpeg';">
              </div>
              <p>${car.model || '2024'}</p>
              <h3>${car.carName}</h3>
              <h2>₹${formatNumber(monthlyRate)} | ₹${formatNumber(car.pricePerDay)}<span> /day</span></h2>
              <a href="javascript:void(0)" onclick="openBookingModal('${car.id}')" class="btn">Rent Now</a>
            </div>
        `;
    }).join('');
}

function handleHeroSearch(e) {
    e.preventDefault();
    const query = document.getElementById('hero-location-search').value.trim();
    loadCars(query);

    const servicesSection = document.getElementById('Services');
    if (servicesSection) {
        servicesSection.style.display = 'block';
        servicesSection.scrollIntoView({ behavior: 'smooth' });
    }
}

// ========================================================
// 4. INDIA-WIDE CASCADING LOCATION SELECTORS
// ========================================================
async function loadStates() {
    try {
        const res = await fetch(`${API_BASE}/api/locations/states`);
        if (!res.ok) return;

        allStates = await res.json();
        populateStateDropdown('book-pickup-state');
        populateStateDropdown('book-drop-state');
    } catch (e) {
        console.error('Error fetching states:', e);
    }
}

function populateStateDropdown(elementId) {
    const select = document.getElementById(elementId);
    if (!select) return;

    select.innerHTML = '<option value="">Select State / UT</option>' +
        allStates.map(s => `<option value="${s.id}" data-name="${s.name}">${s.name}</option>`).join('');
}

async function onPickupStateChange() {
    const stateSelect = document.getElementById('book-pickup-state');
    const stateId = stateSelect.value;
    const citySelect = document.getElementById('book-pickup-city');
    const locSelect = document.getElementById('book-pickup-location');

    citySelect.innerHTML = '<option value="">Select City</option>';
    locSelect.innerHTML = '<option value="">Select Spot</option>';

    if (!stateId) return;

    try {
        const res = await fetch(`${API_BASE}/api/locations/cities/${stateId}`);
        const cities = await res.json();
        citySelect.innerHTML = '<option value="">Select City</option>' +
            cities.map(c => `<option value="${c.id}" data-name="${c.name}">${c.name}</option>`).join('');
    } catch (e) {
        console.error('Error fetching cities:', e);
    }
}

async function onPickupCityChange() {
    const citySelect = document.getElementById('book-pickup-city');
    const cityId = citySelect.value;
    const locSelect = document.getElementById('book-pickup-location');

    locSelect.innerHTML = '<option value="">Select Spot</option>';
    if (!cityId) return;

    try {
        const res = await fetch(`${API_BASE}/api/locations/${cityId}`);
        const locs = await res.json();
        locSelect.innerHTML = '<option value="">Select Spot</option>' +
            locs.map(l => `<option value="${l.name}" data-address="${l.address}">${l.name} - ${l.address}</option>`).join('');
    } catch (e) {
        console.error('Error fetching spots:', e);
    }
}

async function onDropStateChange() {
    const stateSelect = document.getElementById('book-drop-state');
    const stateId = stateSelect.value;
    const citySelect = document.getElementById('book-drop-city');
    const locSelect = document.getElementById('book-drop-location');

    citySelect.innerHTML = '<option value="">Select City</option>';
    locSelect.innerHTML = '<option value="">Select Spot</option>';

    if (!stateId) return;

    try {
        const res = await fetch(`${API_BASE}/api/locations/cities/${stateId}`);
        const cities = await res.json();
        citySelect.innerHTML = '<option value="">Select City</option>' +
            cities.map(c => `<option value="${c.id}" data-name="${c.name}">${c.name}</option>`).join('');
    } catch (e) {
        console.error('Error fetching drop cities:', e);
    }
}

async function onDropCityChange() {
    const citySelect = document.getElementById('book-drop-city');
    const cityId = citySelect.value;
    const locSelect = document.getElementById('book-drop-location');

    locSelect.innerHTML = '<option value="">Select Spot</option>';
    if (!cityId) return;

    try {
        const res = await fetch(`${API_BASE}/api/locations/${cityId}`);
        const locs = await res.json();
        locSelect.innerHTML = '<option value="">Select Spot</option>' +
            locs.map(l => `<option value="${l.name}" data-address="${l.address}">${l.name} - ${l.address}</option>`).join('');
    } catch (e) {
        console.error('Error fetching drop spots:', e);
    }
}

// ========================================================
// 5. BOOKING WORKFLOW & PRICE CALCULATION
// ========================================================
function openBookingModal(carId) {
    const user = getCurrentUser();
    if (!user) {
        showToast("Please sign in or register to book a car.", "info");
        openModal('signin-modal');
        return;
    }

    const car = currentCars.find(c => c.id === carId);
    if (!car) return;

    selectedCarForBooking = car;
    document.getElementById('book-car-id').value = car.id;

    const summaryEl = document.getElementById('booking-car-summary');
    const imgPath = (car.image && (car.image.startsWith('/uploads/') || car.image.startsWith('uploads/'))) 
        ? (car.image.startsWith('/') ? `${API_BASE}${car.image}` : `${API_BASE}/${car.image}`) 
        : (car.image || 'Defender1.jpeg');

    summaryEl.innerHTML = `
        <div style="display:flex; align-items:center; gap:12px;">
            <img src="${imgPath}" style="width:70px; height:50px; object-fit:cover; border-radius:0.4rem;" onerror="this.src='Defender1.jpeg'">
            <div>
                <strong style="color:var(--text-color); font-size:1rem;">${car.brand} ${car.carName}</strong>
                <div style="font-size:0.8rem; color:#666;">${car.category} • ${car.fuelType} • ${car.transmission} • ${car.seats} Seats</div>
            </div>
        </div>
        <div style="text-align:right;">
            <span style="font-size:0.8rem; color:#666;">Daily Rate</span>
            <div style="font-size:1.1rem; font-weight:700; color:var(--main-color);">₹${formatNumber(car.pricePerDay)}/day</div>
        </div>
    `;

    // Inherit dates from hero form or default to today + tomorrow
    const heroP = document.getElementById('hero-pickup-date').value;
    const heroR = document.getElementById('hero-return-date').value;
    const today = new Date().toISOString().split('T')[0];
    const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];

    const pInput = document.getElementById('book-pickup-date');
    const rInput = document.getElementById('book-return-date');
    pInput.min = today;
    rInput.min = today;
    pInput.value = heroP || today;
    rInput.value = heroR || tomorrow;

    // Preselect Maharashtra / Mumbai / Airport by default if available
    prefillLocations();

    calculateBookingPrice();
    openModal('booking-modal');
}

async function prefillLocations() {
    const maharashtra = allStates.find(s => s.name.toLowerCase().includes('maharashtra'));
    if (maharashtra) {
        document.getElementById('book-pickup-state').value = maharashtra.id;
        document.getElementById('book-drop-state').value = maharashtra.id;
        await onPickupStateChange();
        await onDropStateChange();

        const pickupCitySelect = document.getElementById('book-pickup-city');
        const dropCitySelect = document.getElementById('book-drop-city');
        if (pickupCitySelect.options.length > 1) pickupCitySelect.selectedIndex = 1;
        if (dropCitySelect.options.length > 1) dropCitySelect.selectedIndex = 1;

        await onPickupCityChange();
        await onDropCityChange();

        const pickupLocSelect = document.getElementById('book-pickup-location');
        const dropLocSelect = document.getElementById('book-drop-location');
        if (pickupLocSelect.options.length > 1) pickupLocSelect.selectedIndex = 1;
        if (dropLocSelect.options.length > 1) dropLocSelect.selectedIndex = 1;
    }
}

function calculateBookingPrice() {
    if (!selectedCarForBooking) return;

    const pDateVal = document.getElementById('book-pickup-date').value;
    const rDateVal = document.getElementById('book-return-date').value;

    if (!pDateVal || !rDateVal) return;

    const pDate = new Date(pDateVal);
    const rDate = new Date(rDateVal);

    const diffTime = rDate.getTime() - pDate.getTime();
    let diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    if (diffDays <= 0) diffDays = 1;

    const total = diffDays * selectedCarForBooking.pricePerDay;

    document.getElementById('calc-days').textContent = `${diffDays} day(s)`;
    document.getElementById('calc-total').textContent = `₹${formatNumber(total)}`;
}

async function handleBookingSubmit(e) {
    e.preventDefault();

    const carId = document.getElementById('book-car-id').value;
    const pickupStateEl = document.getElementById('book-pickup-state');
    const pickupCityEl = document.getElementById('book-pickup-city');
    const pickupLocEl = document.getElementById('book-pickup-location');

    const dropStateEl = document.getElementById('book-drop-state');
    const dropCityEl = document.getElementById('book-drop-city');
    const dropLocEl = document.getElementById('book-drop-location');

    const pickupDate = document.getElementById('book-pickup-date').value;
    const returnDate = document.getElementById('book-return-date').value;

    const payload = {
        carId: String(carId),
        pickupState: pickupStateEl.options[pickupStateEl.selectedIndex]?.getAttribute('data-name') || pickupStateEl.value,
        pickupCity: pickupCityEl.options[pickupCityEl.selectedIndex]?.getAttribute('data-name') || pickupCityEl.value,
        pickupLocation: pickupLocEl.value,
        dropState: dropStateEl.options[dropStateEl.selectedIndex]?.getAttribute('data-name') || dropStateEl.value,
        dropCity: dropCityEl.options[dropCityEl.selectedIndex]?.getAttribute('data-name') || dropCityEl.value,
        dropLocation: dropLocEl.value,
        pickupDate: pickupDate,
        returnDate: returnDate
    };

    try {
        const res = await fetch(`${API_BASE}/api/bookings`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (!res.ok) {
            throw new Error(data.message || 'Booking submission failed');
        }

        closeModal('booking-modal');
        showToast('Ride booking submitted successfully! Status: PENDING (awaiting admin confirmation)', 'success');
        openMyBookings();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ========================================================
// 6. USER BOOKING HISTORY & CANCELLATION
// ========================================================
async function openMyBookings() {
    const user = getCurrentUser();
    if (!user) {
        openModal('signin-modal');
        return;
    }

    const listEl = document.getElementById('my-bookings-list');
    listEl.innerHTML = '<p style="text-align:center; padding:20px; color:#666;">Loading your bookings...</p>';
    openModal('my-bookings-modal');

    try {
        const res = await fetch(`${API_BASE}/api/bookings/my`, {
            headers: getAuthHeaders()
        });

        if (!res.ok) {
            throw new Error('Failed to fetch bookings');
        }

        const bookings = await res.json();
        renderMyBookings(bookings);
    } catch (err) {
        listEl.innerHTML = `<p style="color:red; text-align:center; padding:20px;">${err.message}</p>`;
    }
}

function renderMyBookings(bookings) {
    const listEl = document.getElementById('my-bookings-list');
    if (!bookings || bookings.length === 0) {
        listEl.innerHTML = `
            <div style="text-align:center; padding:40px; color:#666;">
                <i class='bx bx-calendar-x' style="font-size:3rem; color:#fe5b3d;"></i>
                <h3 style="margin-top:10px;">No Bookings Found</h3>
                <p style="margin-top:5px;">Explore our deals and rent your favorite car today!</p>
                <a href="#Services" onclick="closeModal('my-bookings-modal')" class="btn-sm btn-edit" style="display:inline-block; margin-top:15px; padding:10px 20px;">Explore Fleet</a>
            </div>
        `;
        return;
    }

    listEl.innerHTML = bookings.map(b => {
        const statusBadgeClass = `badge-${b.bookingStatus.toLowerCase()}`;
        const canCancel = (b.bookingStatus === 'PENDING' || b.bookingStatus === 'ACCEPTED');

        return `
            <div class="booking-item-card">
                <div class="bih-top">
                    <div>
                        <strong style="font-size:1.1rem; color:var(--text-color);">${b.carBrand} ${b.carName}</strong>
                        <span style="color:#666; font-size:0.85rem; margin-left:8px;">#BK-${b.id}</span>
                    </div>
                    <div>
                        <span class="badge ${statusBadgeClass}">${b.bookingStatus}</span>
                    </div>
                </div>
                <div class="bih-grid">
                    <div><strong>📍 Pickup:</strong> ${b.pickupLocation}, ${b.pickupCity}, ${b.pickupState}</div>
                    <div><strong>🎯 Drop:</strong> ${b.dropLocation}, ${b.dropCity}, ${b.dropState}</div>
                    <div><strong>📅 Dates:</strong> ${b.pickupDate} to ${b.returnDate} (${b.totalDays} days)</div>
                    <div><strong>💰 Total:</strong> <span style="color:var(--main-color); font-weight:700;">₹${formatNumber(b.totalAmount)}</span> (${b.paymentStatus})</div>
                </div>
                ${b.adminNote ? `<div style="margin-top:8px; font-size:0.85rem; color:#b91c1c; background:#fee2e2; padding:6px 10px; border-radius:0.35rem;"><strong>Admin Note:</strong> ${b.adminNote}</div>` : ''}
                ${canCancel ? `
                    <div style="margin-top:10px; text-align:right;">
                        <button class="btn-sm btn-delete" onclick="cancelBooking('${b.id}')">Cancel Booking</button>
                    </div>
                ` : ''}
            </div>
        `;
    }).join('');
}

async function cancelBooking(bookingId) {
    if (!confirm('Are you sure you want to cancel this booking?')) return;

    try {
        const res = await fetch(`${API_BASE}/api/bookings/${bookingId}/cancel`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });

        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to cancel booking');

        showToast('Booking cancelled successfully', 'info');
        openMyBookings();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

function openAdminPanel() {
    const user = getCurrentUser();
    if (!user || user.role !== 'ROLE_ADMIN') {
        showToast('Admin authorization required', 'error');
        return;
    }
    window.location.href = '/admin';
}

async function loadAdminStats() {
    try {
        const res = await fetch(`${API_BASE}/api/admin/dashboard/stats`, {
            headers: getAuthHeaders()
        });
        if (!res.ok) return;

        const s = await res.json();
        document.getElementById('stat-users').textContent = s.totalUsers;
        document.getElementById('stat-cars').textContent = s.totalCars;
        document.getElementById('stat-pending').textContent = s.pendingRides;
        document.getElementById('stat-accepted').textContent = s.acceptedRides;
        document.getElementById('stat-denied').textContent = s.deniedRides;
        document.getElementById('stat-rev').textContent = `₹${formatNumber(s.totalRevenue)}`;
    } catch (e) {
        console.error('Stats load error:', e);
    }
}

async function loadAdminRides() {
    const tbody = document.getElementById('admin-rides-tbody');
    try {
        const res = await fetch(`${API_BASE}/api/admin/bookings`, {
            headers: getAuthHeaders()
        });
        const rides = await res.json();

        if (!rides || rides.length === 0) {
            tbody.innerHTML = `<tr><td colspan="10" style="text-align:center; padding:20px;">No ride requests yet.</td></tr>`;
            return;
        }

        tbody.innerHTML = rides.map(r => `
            <tr>
                <td>#${r.id}</td>
                <td><strong>${r.userName}</strong></td>
                <td>${r.userMobile}<br><small style="color:#666;">${r.userEmail}</small></td>
                <td>${r.carBrand} ${r.carName}</td>
                <td>${r.pickupCity}<br><small style="color:#666;">${r.pickupLocation}</small></td>
                <td>${r.dropCity}<br><small style="color:#666;">${r.dropLocation}</small></td>
                <td>${r.pickupDate} → ${r.returnDate} (${r.totalDays}d)</td>
                <td><strong>₹${formatNumber(r.totalAmount)}</strong></td>
                <td><span class="badge badge-${r.bookingStatus.toLowerCase()}">${r.bookingStatus}</span></td>
                <td>
                    <div class="action-btns">
                        ${r.bookingStatus === 'PENDING' ? `
                            <button class="btn-sm btn-accept" onclick="acceptRide(${r.id})">Accept</button>
                            <button class="btn-sm btn-deny" onclick="promptDenyRide(${r.id})">Deny</button>
                        ` : ''}
                        ${r.bookingStatus === 'ACCEPTED' ? `
                            <button class="btn-sm btn-edit" onclick="completeRide(${r.id})">Complete</button>
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

        showToast(`Ride #${bookingId} accepted!`, 'success');
        loadAdminRides();
        loadAdminStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

function promptDenyRide(bookingId) {
    document.getElementById('deny-booking-id').value = bookingId;
    document.getElementById('deny-reason').value = '';
    openModal('deny-modal');
}

async function submitDenial(e) {
    e.preventDefault();
    const bookingId = document.getElementById('deny-booking-id').value;
    const adminNote = document.getElementById('deny-reason').value.trim();

    try {
        const res = await fetch(`${API_BASE}/api/admin/bookings/${bookingId}/deny`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify({ adminNote })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to deny ride');

        closeModal('deny-modal');
        showToast(`Ride #${bookingId} denied`, 'info');
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

        showToast(`Ride #${bookingId} marked as completed`, 'success');
        loadAdminRides();
        loadAdminStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// --------------------------------------------------------
// Admin Fleet Management & Direct Add Car
// --------------------------------------------------------
async function loadAdminCars() {
    const tbody = document.getElementById('admin-cars-tbody');
    try {
        const res = await fetch(`${API_BASE}/api/admin/cars`, {
            headers: getAuthHeaders()
        });
        const cars = await res.json();

        tbody.innerHTML = cars.map(c => {
            const imgPath = (c.image && (c.image.startsWith('/uploads/') || c.image.startsWith('uploads/'))) ? (c.image.startsWith('/') ? `${API_BASE}${c.image}` : `${API_BASE}/${c.image}`) : (c.image || 'Defender1.jpeg');
            return `
                <tr>
                    <td>#${c.id}</td>
                    <td><img src="${imgPath}" style="width:50px; height:35px; object-fit:cover; border-radius:0.25rem;" onerror="this.src='Defender1.jpeg'"></td>
                    <td><strong>${c.brand}</strong> ${c.carName} (${c.model})</td>
                    <td>${c.category}</td>
                    <td>₹${formatNumber(c.pricePerDay)}</td>
                    <td>${c.fuelType} • ${c.transmission} • ${c.seats}S</td>
                    <td>
                        <span class="badge ${c.available ? 'badge-accepted' : 'badge-denied'}" style="cursor:pointer;" onclick="toggleCarAvail(${c.id})">
                            ${c.available ? 'Available' : 'Unavailable'}
                        </span>
                    </td>
                    <td>
                        <div class="action-btns">
                            <button class="btn-sm btn-edit" onclick="editCar(${c.id})">Edit</button>
                            <button class="btn-sm btn-delete" onclick="deleteCar(${c.id})">Delete</button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="8" style="color:red; text-align:center;">Failed to load fleet.</td></tr>`;
    }
}

function openAddCarForm() {
    document.getElementById('car-form-title').textContent = 'Add New Car to Fleet';
    document.getElementById('admin-car-form').reset();
    document.getElementById('admin-car-id').value = '';
    document.getElementById('admin-car-avail').checked = true;
    document.getElementById('admin-car-form-wrapper').style.display = 'block';
}

function closeAddCarForm() {
    document.getElementById('admin-car-form-wrapper').style.display = 'none';
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

        document.getElementById('admin-car-image').value = data.imageUrl;
        showToast('Image uploaded successfully!', 'success');
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function handleSaveCar(e) {
    e.preventDefault();

    const id = document.getElementById('admin-car-id').value;
    const payload = {
        brand: document.getElementById('admin-car-brand').value.trim(),
        carName: document.getElementById('admin-car-name').value.trim(),
        model: document.getElementById('admin-car-model').value.trim(),
        category: document.getElementById('admin-car-category').value,
        pricePerDay: parseFloat(document.getElementById('admin-car-price').value),
        fuelType: document.getElementById('admin-car-fuel').value,
        transmission: document.getElementById('admin-car-trans').value,
        seats: parseInt(document.getElementById('admin-car-seats').value),
        image: document.getElementById('admin-car-image').value.trim() || 'Defender1.jpeg',
        description: document.getElementById('admin-car-desc').value.trim(),
        available: document.getElementById('admin-car-avail').checked
    };

    try {
        const url = id ? `${API_BASE}/api/admin/cars/${id}` : `${API_BASE}/api/admin/cars`;
        const method = id ? 'PUT' : 'POST';

        const res = await fetch(url, {
            method: method,
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to save car');

        showToast(id ? 'Car details updated!' : 'New car added to fleet successfully!', 'success');
        closeAddCarForm();
        loadAdminCars();
        loadAdminStats();
        loadCars(); // Instantly update user-side dynamic listing
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function editCar(carId) {
    try {
        const res = await fetch(`${API_BASE}/api/cars/${carId}`);
        const car = await res.json();

        document.getElementById('car-form-title').textContent = `Edit Car #${car.id} - ${car.brand} ${car.carName}`;
        document.getElementById('admin-car-id').value = car.id;
        document.getElementById('admin-car-brand').value = car.brand;
        document.getElementById('admin-car-name').value = car.carName;
        document.getElementById('admin-car-model').value = car.model;
        document.getElementById('admin-car-category').value = car.category;
        document.getElementById('admin-car-price').value = car.pricePerDay;
        document.getElementById('admin-car-fuel').value = car.fuelType;
        document.getElementById('admin-car-trans').value = car.transmission;
        document.getElementById('admin-car-seats').value = car.seats;
        document.getElementById('admin-car-image').value = car.image || '';
        document.getElementById('admin-car-desc').value = car.description || '';
        document.getElementById('admin-car-avail').checked = car.available;

        document.getElementById('admin-car-form-wrapper').style.display = 'block';
        document.getElementById('admin-car-form-wrapper').scrollIntoView({ behavior: 'smooth' });
    } catch (e) {
        showToast('Error loading car details', 'error');
    }
}

async function deleteCar(carId) {
    if (!confirm('Are you sure you want to delete this car from the system?')) return;

    try {
        const res = await fetch(`${API_BASE}/api/admin/cars/${carId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to delete car');

        showToast('Car removed from fleet', 'info');
        loadAdminCars();
        loadAdminStats();
        loadCars();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function toggleCarAvail(carId) {
    try {
        const res = await fetch(`${API_BASE}/api/admin/cars/${carId}/toggle-availability`, {
            method: 'PATCH',
            headers: getAuthHeaders()
        });

        if (!res.ok) throw new Error('Failed to update availability');

        showToast('Car availability toggled', 'success');
        loadAdminCars();
        loadCars();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// --------------------------------------------------------
// Admin Users Listing
// --------------------------------------------------------
async function loadAdminUsers() {
    const tbody = document.getElementById('admin-users-tbody');
    try {
        const res = await fetch(`${API_BASE}/api/admin/users`, {
            headers: getAuthHeaders()
        });
        const users = await res.json();

        tbody.innerHTML = users.map(u => `
            <tr>
                <td>#${u.id}</td>
                <td><strong>${u.name}</strong></td>
                <td>${u.email}</td>
                <td>${u.mobileNumber}</td>
                <td><span class="badge ${u.role === 'ROLE_ADMIN' ? 'badge-completed' : 'badge-pending'}">${u.role}</span></td>
                <td><strong>${u.totalBookings}</strong> ride(s)</td>
                <td>${u.createdAt ? u.createdAt.substring(0, 10) : 'N/A'}</td>
            </tr>
        `).join('');
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="7" style="color:red; text-align:center;">Failed to load users.</td></tr>`;
    }
}

function switchAdminTab(tabId, btn) {
    document.querySelectorAll('.admin-tab-content').forEach(el => el.style.display = 'none');
    document.querySelectorAll('.admin-tab-btn').forEach(el => el.classList.remove('active'));

    document.getElementById(tabId).style.display = 'block';
    if (btn) btn.classList.add('active');
}

// ========================================================
// 8. MODAL & TOAST HELPERS
// ========================================================
function openModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.add('active');
}

function closeModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.remove('active');
}

function switchModal(fromId, toId) {
    closeModal(fromId);
    openModal(toId);
}

// Close modals when clicking backdrop
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.classList.remove('active');
    }
});

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    let icon = 'bx-info-circle';
    if (type === 'success') icon = 'bx-check-circle';
    if (type === 'error') icon = 'bx-error-circle';

    toast.innerHTML = `<i class='bx ${icon}' style="font-size:1.3rem;"></i> <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        toast.style.transition = '0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

function formatNumber(num) {
    if (!num) return '0';
    return Number(num).toLocaleString('en-IN');
}

function handleSubscribe(e) {
    e.preventDefault();
    showToast("Thank you for subscribing to our newsletter!", "success");
}
