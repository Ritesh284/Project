package com.carrental.config;

import com.carrental.entity.*;
import com.carrental.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.admin.email:waghritesh907@gmail.com}")
    private String adminEmail;

    @org.springframework.beans.factory.annotation.Value("${app.admin.mobile:9022165093}")
    private String adminMobile;

    @org.springframework.beans.factory.annotation.Value("${app.admin.name:System Admin}")
    private String adminName;

    public DataInitializer(UserRepository userRepository,
                           CarRepository carRepository,
                           StateRepository stateRepository,
                           CityRepository cityRepository,
                           LocationRepository locationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.locationRepository = locationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initUsers();
        initLocations();
        initCars();
    }

    private void initUsers() {
        // Admin Account (Upsert: admin password is NOT stored in database; it is managed in .env file)
        User admin = userRepository.findByEmail(adminEmail)
                .or(() -> userRepository.findByMobileNumber(adminMobile))
                .orElse(new User());

        admin.setName(adminName);
        admin.setEmail(adminEmail);
        admin.setMobileNumber(adminMobile);
        admin.setPassword(null); // Explicitly null in database: password is authenticated strictly via .env
        admin.setRole(Role.ROLE_ADMIN);
        userRepository.save(admin);

        // Demo User Account (Upsert)
        String userEmail = "user@carrental.com";
        String userMobile = "9876543211";
        String userName = "Rahul Sharma";
        String userPass = "User@123";

        User demoUser = userRepository.findByEmail(userEmail)
                .or(() -> userRepository.findByMobileNumber(userMobile))
                .orElse(new User());

        demoUser.setName(userName);
        demoUser.setEmail(userEmail);
        demoUser.setMobileNumber(userMobile);
        demoUser.setPassword(passwordEncoder.encode(userPass));
        demoUser.setRole(Role.ROLE_USER);
        userRepository.save(demoUser);
    }

    private void initLocations() {
        if (stateRepository.count() > 0) {
            return;
        }

        // Maharashtra
        createStateWithCitiesAndLocations("Maharashtra", List.of(
                new CityData("Mumbai", List.of(
                        new LocData("Chhatrapati Shivaji Maharaj Intl Airport (T2)", "Sahar Road, Andheri East"),
                        new LocData("Domestic Airport (T1)", "Vile Parle East"),
                        new LocData("Bandra Kurla Complex (BKC)", "G Block BKC, Bandra East"),
                        new LocData("Andheri Railway Station West", "Station Road, Andheri West"),
                        new LocData("Navi Mumbai Vashi Hub", "Sector 17, Vashi, Navi Mumbai"),
                        new LocData("Thane West Majiwada Junction", "Ghodbunder Road, Thane")
                )),
                new CityData("Pune", List.of(
                        new LocData("Pune International Airport (PNQ)", "Lohegaon, Pune"),
                        new LocData("Pune Railway Station", "Camp Area, Pune"),
                        new LocData("Hinjawadi IT Park Phase 1", "Hinjawadi, Pune"),
                        new LocData("Viman Nagar Phoenix Mall Hub", "Viman Nagar, Pune"),
                        new LocData("Kothrud Depot", "Paud Road, Kothrud")
                )),
                new CityData("Nashik", List.of(
                        new LocData("Mumbai Naka Circle", "Mumbai-Agra Highway, Nashik"),
                        new LocData("College Road Center", "College Road, Nashik"),
                        new LocData("CIDCO Bus Terminus", "Trimurti Chowk, CIDCO, Nashik")
                )),
                new CityData("Nagpur", List.of(
                        new LocData("Dr. Babasaheb Ambedkar Intl Airport", "Wardha Road, Nagpur"),
                        new LocData("Nagpur Central Railway Station", "Sitabuldi, Nagpur"),
                        new LocData("Dharampeth West", "West High Court Road, Nagpur")
                )),
                new CityData("Chhatrapati Sambhajinagar", List.of(
                        new LocData("Chikalthana Airport", "Jalna Road, Sambhajinagar"),
                        new LocData("Central Bus Station", "Kranti Chowk, Sambhajinagar"),
                        new LocData("Railway Station Stand", "Station Road, Sambhajinagar")
                )),
                new CityData("Ahilyanagar", List.of(
                        new LocData("Savedi Circle Hub", "Savedi, Ahilyanagar"),
                        new LocData("Station Road Stand", "Railway Station Road, Ahilyanagar")
                )),
                new CityData("Kolhapur", List.of(
                        new LocData("Central Bus Stand (CBS)", "Tarabai Park, Kolhapur"),
                        new LocData("Mahalaxmi Temple Hub", "Bhavani Mandap, Kolhapur")
                )),
                new CityData("Solapur", List.of(
                        new LocData("Solapur City Bus Stand", "Old Pune Naka, Solapur"),
                        new LocData("Railway Station Stand", "Station Road, Solapur")
                ))
        ));

        // Karnataka
        createStateWithCitiesAndLocations("Karnataka", List.of(
                new CityData("Bengaluru", List.of(
                        new LocData("Kempegowda International Airport (BLR)", "Devanahalli, Bengaluru"),
                        new LocData("Koramangala 5th Block", "Near Sony World Signal, Koramangala"),
                        new LocData("Indiranagar 100 Feet Road", "100ft Road, Indiranagar"),
                        new LocData("Whitefield ITPL Main Gate", "Whitefield, Bengaluru"),
                        new LocData("Electronic City Phase 1", "Hosur Road, Electronic City")
                )),
                new CityData("Mysuru", List.of(
                        new LocData("Mysuru Palace North Gate", "Sayyaji Rao Road, Mysuru"),
                        new LocData("Mysuru Railway Station", "Medar Block, Mysuru")
                )),
                new CityData("Mangaluru", List.of(
                        new LocData("Mangaluru International Airport (IXE)", "Bajpe, Mangaluru"),
                        new LocData("Hampankatta City Center", "K.S. Rao Road, Mangaluru")
                )),
                new CityData("Hubballi", List.of(
                        new LocData("Hubballi Airport", "Gokul Road, Hubballi"),
                        new LocData("Old Bus Stand", "Station Road, Hubballi")
                )),
                new CityData("Belagavi", List.of(
                        new LocData("Sambre Airport", "Sambre, Belagavi"),
                        new LocData("Cantonment Area", "Camp, Belagavi")
                ))
        ));

        // Gujarat
        createStateWithCitiesAndLocations("Gujarat", List.of(
                new CityData("Ahmedabad", List.of(
                        new LocData("Sardar Vallabhbhai Patel Intl Airport (AMD)", "Hansol, Ahmedabad"),
                        new LocData("SG Highway Iscon Cross Road", "SG Highway, Ahmedabad"),
                        new LocData("Satellite Road Pride Hotel Hub", "Judges Bungalow Road, Satellite"),
                        new LocData("Kalupur Railway Station", "Kalupur, Ahmedabad")
                )),
                new CityData("Surat", List.of(
                        new LocData("Surat International Airport (STV)", "Dumas Road, Surat"),
                        new LocData("Adajan Star Bazaar Hub", "Gaurav Path, Adajan, Surat"),
                        new LocData("Surat Central Railway Station", "Varachha Road, Surat")
                )),
                new CityData("Vadodara", List.of(
                        new LocData("Vadodara Airport (BDQ)", "Harni, Vadodara"),
                        new LocData("Alkapuri RC Dutt Road", "Alkapuri, Vadodara")
                )),
                new CityData("Rajkot", List.of(
                        new LocData("Rajkot Hirasar International Airport", "Hirasar, Rajkot"),
                        new LocData("Yagnik Road", "Yagnik Road, Rajkot")
                )),
                new CityData("Gandhinagar", List.of(
                        new LocData("Sector 11 Infocity Hub", "Infocity, Gandhinagar"),
                        new LocData("Ch-0 Circle", "Sector 1, Gandhinagar")
                ))
        ));

        // Rajasthan
        createStateWithCitiesAndLocations("Rajasthan", List.of(
                new CityData("Jaipur", List.of(
                        new LocData("Jaipur International Airport (JAI)", "Sanganer, Jaipur"),
                        new LocData("Sindhi Camp Bus Station", "Station Road, Jaipur"),
                        new LocData("Malviya Nagar Gaurav Tower", "Malviya Nagar, Jaipur"),
                        new LocData("Vaishali Nagar Amrapali Circle", "Vaishali Nagar, Jaipur")
                )),
                new CityData("Udaipur", List.of(
                        new LocData("Maharana Pratap Airport (UDR)", "Dabok, Udaipur"),
                        new LocData("City Palace Parking Hub", "Old City, Udaipur")
                )),
                new CityData("Jodhpur", List.of(
                        new LocData("Jodhpur Civil Airport", "Air Force Area, Jodhpur"),
                        new LocData("Clock Tower Main Circle", "Nai Sarak, Jodhpur")
                )),
                new CityData("Kota", List.of(
                        new LocData("Vigyan Nagar Flyover Hub", "Jhalawar Road, Kota"),
                        new LocData("Kota Junction", "Bhimganj Mandi, Kota")
                )),
                new CityData("Ajmer", List.of(
                        new LocData("Ajmer Dargah Link Road", "Khwaja Model Town, Ajmer"),
                        new LocData("Pushkar Bypass Point", "Ajmer Pushkar Road")
                ))
        ));

        // Tamil Nadu
        createStateWithCitiesAndLocations("Tamil Nadu", List.of(
                new CityData("Chennai", List.of(
                        new LocData("Chennai International Airport (MAA)", "Meenambakkam, Chennai"),
                        new LocData("T. Nagar Panagal Park", "Prakasam Road, T. Nagar"),
                        new LocData("OMR Tech Corridor Sholinganallur", "Old Mahabalipuram Road, Chennai"),
                        new LocData("Chennai Central Railway Station", "Kannappar Thidal, Chennai")
                )),
                new CityData("Coimbatore", List.of(
                        new LocData("Coimbatore International Airport (CJB)", "Avinashi Road, Peelamedu"),
                        new LocData("Gandhipuram Central Bus Stand", "Gandhipuram, Coimbatore")
                )),
                new CityData("Madurai", List.of(
                        new LocData("Madurai Airport (IXM)", "Avaniyapuram, Madurai"),
                        new LocData("Meenakshi Amman Temple Hub", "West Tower Street, Madurai")
                )),
                new CityData("Salem", List.of(
                        new LocData("New Bus Stand", "Meyyanur, Salem"),
                        new LocData("Salem Junction", "Suramangalam, Salem")
                )),
                new CityData("Tiruchirappalli", List.of(
                        new LocData("Tiruchirappalli Intl Airport (TRZ)", "Old Airport Road, Trichy"),
                        new LocData("Central Bus Stand", "Cantonment, Trichy")
                ))
        ));

        // Telangana
        createStateWithCitiesAndLocations("Telangana", List.of(
                new CityData("Hyderabad", List.of(
                        new LocData("Rajiv Gandhi Intl Airport (HYD)", "Shamshabad, Hyderabad"),
                        new LocData("Hitec City Cyber Towers", "Madhapur, Hyderabad"),
                        new LocData("Gachibowli Financial District", "Gachibowli, Hyderabad"),
                        new LocData("Banjara Hills Road No 12", "Banjara Hills, Hyderabad"),
                        new LocData("Secunderabad Railway Station", "Station Road, Secunderabad")
                )),
                new CityData("Warangal", List.of(
                        new LocData("Kazipet Railway Junction", "Kazipet, Warangal"),
                        new LocData("Hanamkonda Bus Stand", "Hanamkonda, Warangal")
                ))
        ));

        // West Bengal
        createStateWithCitiesAndLocations("West Bengal", List.of(
                new CityData("Kolkata", List.of(
                        new LocData("Netaji Subhash Chandra Bose Intl Airport (CCU)", "Dum Dum, Kolkata"),
                        new LocData("Park Street Metro Station Hub", "Park Street, Kolkata"),
                        new LocData("Salt Lake Sector V Tech Hub", "Salt Lake, Kolkata"),
                        new LocData("Howrah Railway Station", "Howrah Bridge Approach, Kolkata")
                )),
                new CityData("Siliguri", List.of(
                        new LocData("Bagdogra Airport (IXB)", "Bagdogra, Siliguri"),
                        new LocData("New Jalpaiguri (NJP) Station", "Bhakti Nagar, Siliguri")
                ))
        ));

        // Kerala
        createStateWithCitiesAndLocations("Kerala", List.of(
                new CityData("Kochi", List.of(
                        new LocData("Cochin International Airport (COK)", "Nedumbassery, Kochi"),
                        new LocData("MG Road Metro Hub", "Ernakulam South, Kochi"),
                        new LocData("Kakkanad Infopark Gate", "Infopark Road, Kakkanad")
                )),
                new CityData("Thiruvananthapuram", List.of(
                        new LocData("Trivandrum International Airport (TRV)", "Chacka, Thiruvananthapuram"),
                        new LocData("Technopark Phase 1", "Kazhakkoottam, Trivandrum")
                )),
                new CityData("Kozhikode", List.of(
                        new LocData("Calicut International Airport (CCJ)", "Karipur, Kozhikode"),
                        new LocData("Kozhikode Beach Point", "Beach Road, Kozhikode")
                )),
                new CityData("Thrissur", List.of(
                        new LocData("Swaraj Round North", "Swaraj Round, Thrissur"),
                        new LocData("Thrissur Railway Station", "Poothole, Thrissur")
                ))
        ));

        // Uttar Pradesh
        createStateWithCitiesAndLocations("Uttar Pradesh", List.of(
                new CityData("Lucknow", List.of(
                        new LocData("Chaudhary Charan Singh Airport (LKO)", "Amausi, Lucknow"),
                        new LocData("Gomti Nagar Patrakarpuram", "Gomti Nagar, Lucknow"),
                        new LocData("Hazratganj Main Market", "Hazratganj, Lucknow"),
                        new LocData("Charbagh Railway Station", "Charbagh, Lucknow")
                )),
                new CityData("Noida", List.of(
                        new LocData("Sector 18 Atta Market", "Sector 18, Noida"),
                        new LocData("Sector 62 Electronic City Metro", "Sector 62, Noida"),
                        new LocData("Noida Expressway Advant Hub", "Sector 142, Noida")
                )),
                new CityData("Agra", List.of(
                        new LocData("Taj Mahal East Gate Hub", "Taj East Gate Road, Agra"),
                        new LocData("Agra Cantt Railway Station", "Cantt, Agra")
                )),
                new CityData("Varanasi", List.of(
                        new LocData("Lal Bahadur Shastri Airport (VNS)", "Babatpur, Varanasi"),
                        new LocData("Godowlia Chowk", "Godowlia, Varanasi")
                )),
                new CityData("Kanpur", List.of(
                        new LocData("Kanpur Central Railway Station", "Cantonment, Kanpur"),
                        new LocData("Mall Road Z Square Hub", "Civil Lines, Kanpur")
                )),
                new CityData("Prayagraj", List.of(
                        new LocData("Civil Lines Bus Stand", "MG Marg, Prayagraj"),
                        new LocData("Sangam Ghat Hub", "Daraganj, Prayagraj")
                ))
        ));

        // Goa
        createStateWithCitiesAndLocations("Goa", List.of(
                new CityData("Panaji", List.of(
                        new LocData("Panjim Bus Stand (Kadamba)", "Patto Centre, Panaji"),
                        new LocData("Fontainhas Heritage Quarter", "Altinho, Panaji"),
                        new LocData("Miramar Beach Point", "Miramar, Panaji")
                )),
                new CityData("Margao", List.of(
                        new LocData("Madgaon Railway Station", "Aquem, Margao"),
                        new LocData("Margao Municipal Garden", "Abade Faria Road, Margao")
                )),
                new CityData("Vasco da Gama", List.of(
                        new LocData("Dabolim International Airport (GOI)", "Dabolim, Vasco"),
                        new LocData("Vasco Railway Station", "Swatantra Path, Vasco")
                )),
                new CityData("North Goa - Mopa", List.of(
                        new LocData("Manohar International Airport (GOX)", "Mopa, Pernem"),
                        new LocData("Calangute Beach Circle", "Calangute-Baga Road")
                ))
        ));

        // Delhi (NCT)
        createStateWithCitiesAndLocations("Delhi (NCT)", List.of(
                new CityData("New Delhi", List.of(
                        new LocData("Indira Gandhi International Airport (T3)", "Aerocity, New Delhi"),
                        new LocData("Connaught Place (Inner Circle)", "Block A, Connaught Place"),
                        new LocData("South Extension Part 2", "Ring Road, South Ext"),
                        new LocData("Dwarka Sector 21 Metro Station", "Dwarka, New Delhi"),
                        new LocData("New Delhi Railway Station (Ajmeri Gate)", "Ajmeri Gate, New Delhi")
                ))
        ));

        // Additional States & Union Territories
        createStateWithCitiesAndLocations("Punjab", List.of(
                new CityData("Amritsar", List.of(
                        new LocData("Sri Guru Ram Dass Jee Intl Airport", "Raja Sansi, Amritsar"),
                        new LocData("Golden Temple Hub", "Heritage Street, Amritsar")
                )),
                new CityData("Ludhiana", List.of(
                        new LocData("Ferozepur Road Westend Mall", "Ludhiana West"),
                        new LocData("Ludhiana Junction", "GT Road, Ludhiana")
                )),
                new CityData("Chandigarh / Mohali", List.of(
                        new LocData("Chandigarh International Airport (IXC)", "Mohali Bypass, Chandigarh"),
                        new LocData("Sector 17 Plaza", "Sector 17, Chandigarh")
                ))
        ));

        createStateWithCitiesAndLocations("Haryana", List.of(
                new CityData("Gurugram", List.of(
                        new LocData("Cyber Hub DLF Phase 2", "Cyber City, Gurugram"),
                        new LocData("Golf Course Road One Horizon", "Sector 43, Gurugram"),
                        new LocData("Huda City Centre Metro", "Sector 29, Gurugram")
                )),
                new CityData("Faridabad", List.of(
                        new LocData("Neelam Flyover Metro Hub", "Sector 20, Faridabad")
                ))
        ));

        createStateWithCitiesAndLocations("Madhya Pradesh", List.of(
                new CityData("Indore", List.of(
                        new LocData("Devi Ahilya Bai Holkar Airport (IDR)", "Depalpur Road, Indore"),
                        new LocData("Vijay Nagar Square", "AB Road, Indore")
                )),
                new CityData("Bhopal", List.of(
                        new LocData("Raja Bhoj Airport (BHO)", "Gandhi Nagar, Bhopal"),
                        new LocData("MP Nagar Zone 1", "MP Nagar, Bhopal")
                ))
        ));

        createStateWithCitiesAndLocations("Andhra Pradesh", List.of(
                new CityData("Visakhapatnam", List.of(
                        new LocData("Visakhapatnam International Airport (VTZ)", "NAD Junction"),
                        new LocData("RK Beach Point", "Beach Road, Vizag")
                )),
                new CityData("Vijayawada", List.of(
                        new LocData("Vijayawada Airport (VGA)", "Gannavaram, Vijayawada"),
                        new LocData("Benz Circle", "MG Road, Vijayawada")
                ))
        ));

        createStateWithCitiesAndLocations("Himachal Pradesh", List.of(
                new CityData("Shimla", List.of(
                        new LocData("Shimla Mall Road Lift", "The Mall, Shimla"),
                        new LocData("ISBT Tutikandi", "Tutikandi, Shimla")
                )),
                new CityData("Manali", List.of(
                        new LocData("Manali Mall Road Hub", "Mall Road, Manali"),
                        new LocData("Solang Valley Pickup Spot", "Solang, Manali")
                ))
        ));

        createStateWithCitiesAndLocations("Uttarakhand", List.of(
                new CityData("Dehradun", List.of(
                        new LocData("Jolly Grant Airport (DED)", "Rishikesh Road, Dehradun"),
                        new LocData("Clock Tower Rajpur Road", "Rajpur Road, Dehradun")
                )),
                new CityData("Rishikesh", List.of(
                        new LocData("Laxman Jhula Chowk", "Tapovan, Rishikesh"),
                        new LocData("Triveni Ghat", "Mayakund, Rishikesh")
                ))
        ));

        createStateWithCitiesAndLocations("Jammu & Kashmir", List.of(
                new CityData("Srinagar", List.of(
                        new LocData("Sheikh ul-Alam Intl Airport (SXR)", "Humhama, Srinagar"),
                        new LocData("Dal Lake Boulevard Road", "Boulevard Road, Srinagar")
                )),
                new CityData("Jammu", List.of(
                        new LocData("Jammu Civil Airport (IXJ)", "Satwari, Jammu"),
                        new LocData("Jammu Tawi Station", "Tawi, Jammu")
                ))
        ));

        createStateWithCitiesAndLocations("Assam", List.of(
                new CityData("Guwahati", List.of(
                        new LocData("Lokpriya Gopinath Bordoloi Airport (GAU)", "Borjhar, Guwahati"),
                        new LocData("GS Road Christian Basti", "GS Road, Guwahati")
                ))
        ));

        createStateWithCitiesAndLocations("Bihar", List.of(
                new CityData("Patna", List.of(
                        new LocData("Jay Prakash Narayan Airport (PAT)", "Shaikhpura, Patna"),
                        new LocData("Dak Bungalow Crossing", "Fraser Road, Patna")
                ))
        ));

        createStateWithCitiesAndLocations("Odisha", List.of(
                new CityData("Bhubaneswar", List.of(
                        new LocData("Biju Patnaik International Airport (BBI)", "Airport Road, Bhubaneswar"),
                        new LocData("Janpath Saheed Nagar", "Janpath, Bhubaneswar")
                ))
        ));
    }

    private void createStateWithCitiesAndLocations(String stateName, List<CityData> cities) {
        State state = new State(stateName);
        State savedState = stateRepository.save(state);

        for (CityData cd : cities) {
            City city = new City(cd.name, savedState.getId());
            City savedCity = cityRepository.save(city);

            for (LocData ld : cd.locations) {
                Location loc = new Location(ld.name, ld.address, savedCity.getId());
                locationRepository.save(loc);
            }
        }
    }

    private void initCars() {
        if (carRepository.count() > 0) {
            return;
        }

        List<Car> initialCars = Arrays.asList(
                new Car("Land Rover", "2022", "Defender 110", "Luxury SUV", 1000.0, "Diesel", "Automatic", 7, "Defender1.jpeg", "Iconic luxury all-terrain SUV equipped with superior safety, terrain response system, and ultimate comfort.", true),
                new Car("BMW", "2023", "BMW X7 M-Sport", "Luxury SUV", 1863.0, "Petrol", "Automatic", 7, "BMW.jpeg", "Executive flagship luxury SUV offering twin-turbo performance, sky lounge panoramic roof, and plush 7-seat cabin.", true),
                new Car("Mercedes-Benz", "2024", "Mercedes-Benz GLE", "Luxury SUV", 2000.0, "Diesel", "Automatic", 5, "Mercedez.jpeg", "Supreme comfort and cutting-edge MBUX technology with air suspension for effortless highway cruising.", true),
                new Car("Volvo", "2019", "Volvo XC90 Inscription", "Luxury SUV", 1166.0, "Hybrid", "Automatic", 7, "Volvo.jpeg", "World's safest SUV loaded with Bowers & Wilkins audio, pilot assist, and refined Scandinavian luxury.", true),
                new Car("Toyota", "2025", "Toyota GR Supra 3.0", "Sports Car", 2666.0, "Petrol", "Automatic", 2, "Supra.jpeg", "Pure sports car DNA with 382 horsepower, rear-wheel drive adrenaline, and head-turning styling.", true),
                new Car("Skoda", "2018", "Skoda Octavia vRS", "Premium Sedan", 833.0, "Petrol", "Automatic", 5, "Skoda.jpeg", "Dynamic European sports sedan packed with thrilling performance, huge boot space, and 5-star safety.", true),
                new Car("Audi", "2022", "Audi RS5 Coupe", "Sports Sedan", 1100.0, "Petrol", "Automatic", 4, "Audi.jpeg", "Twin-turbo V6 performance with Quattro all-wheel drive, matrix LED headlights, and aggressive styling.", true),
                new Car("Mini", "2021", "Mini Cooper Countryman S", "Compact Luxury", 866.0, "Petrol", "Automatic", 5, "Minicupoor.jpg", "Spirited go-kart handling, British heritage styling, and premium compact road presence.", true),
                new Car("Lamborghini", "2017", "Lamborghini Huracan EVO", "Supercar", 2566.0, "Petrol", "Automatic", 2, "Lambo.jpeg", "Naturally aspirated V10 supercar unleashing 640 HP, exotic sound, and unmatched visual drama.", true),
                new Car("Toyota", "2017", "Toyota Fortuner 4x4", "SUV", 800.0, "Diesel", "Automatic", 7, "for.jpeg", "Legendary reliability, commanding road presence, high ground clearance, and robust 4x4 capability.", true),
                new Car("Mahindra", "2023", "Mahindra Thar LX 4x4", "Off-Road SUV", 666.0, "Diesel", "Automatic", 4, "Mahindra Thar.jpeg", "Unstoppable true off-roader with convertible/hard-top fun, 4x4 low range, and rugged styling.", true),
                new Car("Mahindra", "2021", "Mahindra Scorpio-N Z8", "SUV", 766.0, "Diesel", "Manual", 7, "Scorpio.jpeg", "Big daddy of SUVs with commanding seating, robust ladder-frame chassis, and smooth diesel torque.", true),
                new Car("Hyundai", "2018", "Hyundai Verna SX Turbo", "Sedan", 600.0, "Petrol", "Manual", 5, "Verna1.jpeg", "Sleek aerodynamic sedan with ventilated front seats, punchy turbo engine, and premium interior.", true),
                new Car("Hyundai", "2022", "Hyundai Creta SX(O)", "Compact SUV", 583.0, "Diesel", "Automatic", 5, "Creta.jpeg", "India's favorite compact SUV with panoramic sunroof, Bose premium sound, and comfortable ride.", true),
                new Car("Maruti Suzuki", "2016", "Maruti Suzuki Swift ZXi+", "Hatchback", 500.0, "Petrol", "Manual", 5, "swift.jpeg", "Agile, peppy, fuel-efficient city hatchback that makes parking and commuting a breeze.", true)
        );

        carRepository.saveAll(initialCars);
    }

    private static class CityData {
        String name;
        List<LocData> locations;

        CityData(String name, List<LocData> locations) {
            this.name = name;
            this.locations = locations;
        }
    }

    private static class LocData {
        String name;
        String address;

        LocData(String name, String address) {
            this.name = name;
            this.address = address;
        }
    }
}
