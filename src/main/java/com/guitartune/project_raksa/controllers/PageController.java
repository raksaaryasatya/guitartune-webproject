package com.guitartune.project_raksa.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guitartune.project_raksa.constant.RoleConstant;
import com.guitartune.project_raksa.dto.LoginRequest;
import com.guitartune.project_raksa.dto.RegisterRequest;
import com.guitartune.project_raksa.dto.store.RegisterStoreRequest;
import com.guitartune.project_raksa.models.Category;
import com.guitartune.project_raksa.models.Product;
import com.guitartune.project_raksa.models.Role;
import com.guitartune.project_raksa.models.Store;
import com.guitartune.project_raksa.models.StoreProduct;
import com.guitartune.project_raksa.models.Transaction;
import com.guitartune.project_raksa.models.User;
import com.guitartune.project_raksa.repositorys.CategoryRepository;
import com.guitartune.project_raksa.repositorys.ProductRepository;
import com.guitartune.project_raksa.repositorys.RoleRepository;
import com.guitartune.project_raksa.repositorys.StoreProductRepository;
import com.guitartune.project_raksa.repositorys.StoreRepository;
import com.guitartune.project_raksa.repositorys.TransactionRepository;
import com.guitartune.project_raksa.repositorys.UserRepository;
import com.guitartune.project_raksa.services.convert.ConvertImage;
import com.guitartune.project_raksa.services.product.ProductService;
import com.guitartune.project_raksa.services.transaction.TransactionService;
import com.guitartune.project_raksa.services.user.UserService;

@Controller
public class PageController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreProductRepository storeProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConvertImage convertImage;

    @GetMapping("/register")
    public String registerPage(Model model) {
        // Membuat objek RegisterRequest untuk digunakan pada form pendaftaran
        RegisterRequest registerRequest = new RegisterRequest();

        // Menambahkan objek registerRequest ke model agar dapat diakses di view (HTML)
        model.addAttribute("registerRequest", registerRequest);

        // Mengembalikan nama file view "register" untuk ditampilkan ke pengguna
        return "register";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        // Mengecek jika ada parameter 'error' dalam URL (menandakan login gagal)
        if (error != null) {
            // Menampilkan log ke konsol jika login gagal
            System.out.println("Login gagal: Invalid username or password.");

            // Menambahkan pesan error ke model untuk ditampilkan di view
            model.addAttribute("message", "Invalid username or password, please try again.");
        }

        // Menambahkan objek LoginRequest baru ke model untuk diikat ke form login
        model.addAttribute("loginRequest", new LoginRequest());

        // Mengembalikan nama file view "login" untuk ditampilkan ke pengguna
        return "login";
    }

    @GetMapping("/home")
    public String homePage(
            @RequestParam(value = "search", required = false) String search, // Menangani parameter pencarian produk
                                                                             // berdasarkan nama
            @RequestParam(value = "category", required = false) String category, // Menangani parameter pencarian
                                                                                 // berdasarkan kategori
            @RequestParam(value = "sort", required = false) String sort, // Menangani parameter untuk pengurutan harga
            @RequestParam(value = "price-range", required = false) String priceRange, // Menangani parameter untuk
                                                                                      // rentang harga produk
            Model model) { // Model digunakan untuk menambahkan data ke view

        // Mengambil nama pengguna yang sedang login menggunakan Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Menemukan data pengguna berdasarkan username
        User user = userRepository.findUserByUsername(username);

        // Daftar produk yang akan ditampilkan di halaman home
        List<Product> products;

        // Filter produk berdasarkan pencarian nama produk jika parameter 'search' ada
        if (search != null && !search.isEmpty()) {
            products = productRepository.findByProductNameStartingWith(search);
        }
        // Filter produk berdasarkan kategori jika parameter 'category' ada
        else if (category != null && !category.isEmpty()) {
            products = productRepository.findByCategory_CategoryName(category);
        }
        // Jika tidak ada filter pencarian, tampilkan semua produk
        else {
            products = productRepository.findAll();
        }

        // Memfilter produk berdasarkan rentang harga jika parameter 'price-range' ada
        if (priceRange != null && !priceRange.isEmpty()) {
            String[] priceParts = priceRange.split("-"); // Mengambil rentang harga dari string "min-max"
            if (priceParts.length == 2) {
                double minPrice = Double.parseDouble(priceParts[0]);
                double maxPrice = Double.parseDouble(priceParts[1]);
                // Memfilter produk dengan harga dalam rentang yang ditentukan
                products = products.stream()
                        .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                        .collect(Collectors.toList());
            } else if (priceRange.equals("2000+")) {
                // Memfilter produk dengan harga lebih dari 2000
                products = products.stream()
                        .filter(product -> product.getPrice() > 2000)
                        .collect(Collectors.toList());
            }
        }

        // Menambahkan pesan jika tidak ada produk ditemukan
        if (products.isEmpty()) {
            model.addAttribute("message", "No products found for the search term!");
        }

        // Sorting produk berdasarkan harga jika parameter 'sort' ada
        if (sort != null && !sort.isEmpty()) {
            if (sort.equals("asc")) {
                // Mengurutkan produk berdasarkan harga secara ascending
                products.sort(Comparator.comparingDouble(Product::getPrice));
            } else if (sort.equals("desc")) {
                // Mengurutkan produk berdasarkan harga secara descending
                products.sort(Comparator.comparingDouble(Product::getPrice).reversed());
            }
        }

        // Mengonversi gambar produk ke format Base64 untuk ditampilkan di halaman
        for (Product product : products) {
            try {
                if (product != null && product.getImageProduct() != null) {
                    String base64Image = convertImage.convertImage(product.getImageProduct());
                    product.setBase64StoreImage(base64Image); // Menyimpan gambar dalam format Base64 ke produk
                }
            } catch (Exception e) {
                model.addAttribute("message", "Error processing product images"); // Menampilkan pesan jika terjadi
                                                                                  // error dalam memproses gambar
                e.printStackTrace();
            }
        }

        // Menambahkan data pengguna dan produk ke model untuk ditampilkan di view
        model.addAttribute("user", user);
        model.addAttribute("products", products);

        // Mengembalikan view 'home/home' yang akan menampilkan produk dan informasi
        // pengguna
        return "home/home";
    }

    ///////////////// ADMIN /////////////////
    @GetMapping("/admin/dashboard")
    public String dashboardPage(@RequestParam(value = "search", required = false) String search, // Parameter untuk
                                                                                                 // pencarian produk
                                                                                                 // berdasarkan nama
            @RequestParam(value = "category", required = false) String category, // Parameter untuk pencarian
                                                                                 // berdasarkan kategori
            @RequestParam(value = "sort", required = false) String sort, // Parameter untuk mengurutkan produk
                                                                         // berdasarkan harga
            @RequestParam(value = "price-range", required = false) String priceRange, // Parameter untuk rentang harga
                                                                                      // produk
            Model model) { // Model untuk menambahkan data yang akan ditampilkan di view

        // Mendapatkan nama pengguna yang sedang login (admin) menggunakan Spring
        // Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Mengambil data pengguna berdasarkan username
        User admin = userRepository.findUserByUsername(username);

        // Mengambil semua data pengguna dan kategori dari repository
        List<User> users = userRepository.findAll();
        List<Category> categories = categoryRepository.findAll();

        // Mendapatkan daftar produk berdasarkan filter pencarian
        List<Product> products;

        // Filter produk berdasarkan nama jika parameter 'search' ada
        if (search != null && !search.isEmpty()) {
            products = productRepository.findByProductNameStartingWith(search);
        }
        // Filter produk berdasarkan kategori jika parameter 'category' ada
        else if (category != null && !category.isEmpty()) {
            products = productRepository.findByCategory_CategoryName(category);
        }
        // Jika tidak ada filter pencarian, tampilkan semua produk
        else {
            products = productRepository.findAll();
        }

        // Memfilter produk berdasarkan rentang harga jika parameter 'price-range' ada
        if (priceRange != null && !priceRange.isEmpty()) {
            String[] priceParts = priceRange.split("-"); // Mengambil rentang harga dari string "min-max"
            if (priceParts.length == 2) {
                double minPrice = Double.parseDouble(priceParts[0]);
                double maxPrice = Double.parseDouble(priceParts[1]);
                // Memfilter produk berdasarkan harga dalam rentang yang ditentukan
                products = products.stream()
                        .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                        .collect(Collectors.toList());
            } else if (priceRange.equals("2000+")) {
                // Memfilter produk dengan harga lebih dari 2000
                products = products.stream()
                        .filter(product -> product.getPrice() > 2000)
                        .collect(Collectors.toList());
            }
        }

        // Menambahkan pesan jika tidak ada produk ditemukan
        if (products.isEmpty()) {
            model.addAttribute("message", "Tidak ada produk yang ditemukan untuk istilah pencarian!");
        }

        // Sorting produk berdasarkan harga jika parameter 'sort' ada
        if (sort != null && !sort.isEmpty()) {
            if (sort.equals("asc")) {
                // Mengurutkan produk berdasarkan harga secara ascending
                products = productRepository.findAllByOrderByPriceAsc();
            } else if (sort.equals("desc")) {
                // Mengurutkan produk berdasarkan harga secara descending
                products = productRepository.findAllByOrderByPriceDesc();
            }
        }

        // Mengonversi gambar produk ke format Base64 agar dapat ditampilkan di halaman
        for (Product product : products) {
            try {
                if (product != null && product.getImageProduct() != null) {
                    String base64Image = convertImage.convertImage(product.getImageProduct());
                    product.setBase64StoreImage(base64Image); // Menyimpan gambar dalam format Base64
                }
            } catch (Exception e) {
                model.addAttribute("message", "Terjadi kesalahan saat memproses gambar produk");
                e.printStackTrace();
            }
        }

        // Menambahkan data pengguna (admin), produk, kategori, dan pengguna lain ke
        // model
        model.addAttribute("users", users);
        model.addAttribute("admin", admin);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);

        // Mengembalikan view 'admin/dashboard' yang akan menampilkan dashboard admin
        return "admin/dashboard";
    }

    @GetMapping("/admin/delete-product/{id}")
    public String deleteProductAdmin(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            // Memanggil service untuk menghapus produk berdasarkan ID
            productService.deleteProduct(id);

            // Menambahkan pesan keberhasilan jika produk berhasil dihapus
            redirectAttributes.addFlashAttribute("message", "Produk berhasil dihapus");
        } catch (Exception e) {
            // Menambahkan pesan kesalahan jika terjadi exception saat menghapus produk
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        // Mengarahkan kembali ke halaman dashboard admin setelah operasi selesai
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String getUsersAdmin(@RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort, Model model) {

        // Mendapatkan nama pengguna yang sedang login
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Mendapatkan user admin berdasarkan username
        User admin = userRepository.findUserByUsername(username);

        // Mendapatkan role admin dari repository
        Role roleAdmin = roleRepository.findRoleByRoleName(RoleConstant.ROLE_ADMIN);

        // Mendapatkan semua pengguna
        List<User> allUsers = userRepository.findAll();

        // Inisialisasi list pengguna non-admin
        List<User> nonAdminUsers = new ArrayList<>();

        // Iterasi untuk menyaring pengguna non-admin
        for (User user : allUsers) {
            if (!user.getRole().equals(roleAdmin)) {
                nonAdminUsers.add(user);
            }
        }

        // Pencarian pengguna berdasarkan username
        if (search != null && !search.isEmpty()) {
            nonAdminUsers = userRepository.findByUsernameStartingWith(search);
        }

        // Sorting berdasarkan username
        if (sort != null && !sort.isEmpty()) {
            if (sort.equals("asc")) {
                nonAdminUsers = userRepository.findAllByOrderByUsernameAsc();
            } else if (sort.equals("desc")) {
                nonAdminUsers = userRepository.findAllByOrderByUsernameDesc();
            }
        }

        // Filter ulang untuk memastikan admin tidak masuk list
        nonAdminUsers = nonAdminUsers.stream()
                .filter(user -> !user.getRole().equals(roleAdmin))
                .collect(Collectors.toList());

        // Menambahkan daftar pengguna non-admin ke model
        model.addAttribute("users", nonAdminUsers);
        model.addAttribute("admin", admin);

        // Mengembalikan nama template HTML untuk ditampilkan
        return "admin/users";
    }

    @GetMapping("/admin/delete-user/{id}")
    public String deleteUser(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            // Memanggil service untuk menghapus user berdasarkan id
            userService.deleteUser(id);

            // Menambahkan pesan sukses ke model menggunakan RedirectAttributes
            redirectAttributes.addFlashAttribute("message", "Berhasil Menghapus User");
        } catch (Exception e) {
            // Menambahkan pesan error jika terjadi exception
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        // Redirect ke halaman daftar pengguna setelah penghapusan
        return "redirect:/admin/users";
    }

    ///////////////// USER /////////////////
    @GetMapping("/user/profile")
    public String userProfilePage(Model model) {
        // Mendapatkan objek authentication dari SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Memeriksa apakah pengguna sudah terautentikasi
        if (authentication != null && authentication.isAuthenticated()) {
            // Mendapatkan nama pengguna yang sedang login
            String username = authentication.getName();

            // Mencari data pengguna berdasarkan username
            User user = userRepository.findUserByUsername(username);

            // Memeriksa apakah pengguna ditemukan di database
            if (user != null) {
                // Menambahkan objek user ke dalam model untuk ditampilkan di view
                model.addAttribute("user", user);
                return "user/profile"; // Menampilkan halaman profil pengguna
            } else {
                // Jika pengguna tidak ditemukan, redirect ke halaman login
                return "redirect:/login";
            }
        } else {
            // Jika pengguna tidak terautentikasi, redirect ke halaman login
            return "redirect:/login";
        }
    }

    @GetMapping("/user/update-profile/{id}")
    public String updateUserPage(@PathVariable(value = "id") String id, Model model) {
        // Ambil pengguna yang sedang login
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Ambil ID pengguna yang sedang login dari database
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            // Jika pengguna tidak ditemukan, arahkan ke halaman login
            return "redirect:/login";
        }

        User loggedInUser = optionalUser.get();

        // Cek apakah ID yang dimasukkan di URL cocok dengan ID pengguna yang sedang
        // login
        if (!loggedInUser.getId().toString().equals(id)) {
            // Jika ID tidak cocok, arahkan ke halaman profil pengguna sendiri
            model.addAttribute("message", "Anda tidak dapat mengakses profil orang lain.");
            return "redirect:/user/profile";
        }

        // Jika ID valid dan pengguna yang sedang login sama dengan ID di URL, tampilkan
        // halaman update
        model.addAttribute("user", loggedInUser);
        return "user/update-profile";
    }

    ///////////////// STORE /////////////////
    @GetMapping("/stores/check-store")
    public String checkUserStore(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findUserByUsername(username);

            // Cek apakah user memiliki store
            Store userStore = storeRepository.findStoreByUser(user);
            if (userStore != null) {
                // User memiliki store
                return "redirect:/stores/home-store"; // Halaman jika sudah memiliki store
            } else {
                // User belum memiliki store
                return "redirect:/stores/register-store"; // Halaman untuk membuat store
            }
        } else {
            return "redirect:/login"; // Pengguna belum login, arahkan ke halaman login
        }
    }

    @GetMapping("/stores/register-store")
    public String registerStorePage(Model model) {
        RegisterStoreRequest registerStoreRequest = new RegisterStoreRequest();
        model.addAttribute("registerStoreRequest", registerStoreRequest);
        return "store/register-store";
    }

    @GetMapping("/stores/home-store")
    public String homeStorePage(Model model) {
        // Mendapatkan informasi autentikasi pengguna yang sedang login
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Mengecek apakah pengguna terautentikasi
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findUserByUsername(username);

            // Memeriksa apakah pengguna ditemukan
            if (user == null) {
                model.addAttribute("message", "User not found");
                return "redirect:/login"; // Jika pengguna tidak ditemukan, arahkan ke halaman login
            }

            // Mencari toko milik pengguna
            Store userStore = storeRepository.findStoreByUser(user);

            // Memeriksa apakah toko ditemukan
            if (userStore == null) {
                model.addAttribute("message", "Store not found");
                return "redirect:/login"; // Jika toko tidak ditemukan, arahkan ke halaman login
            }

            // Mengonversi gambar toko jika ada
            if (userStore.getStoreImage() != null) {
                try {
                    String storeImage = convertImage.convertImage(userStore.getStoreImage());
                    userStore.setBase64StoreImage(storeImage); // Menyimpan gambar toko dalam format Base64
                } catch (Exception e) {
                    model.addAttribute("message", "Error converting store image");
                    e.printStackTrace(); // Menangani error saat mengonversi gambar toko
                }
            }

            // Mengambil semua produk yang terkait dengan toko
            List<StoreProduct> storeProducts = storeProductRepository.findAllByStore(userStore);
            List<StoreProduct> productsWithImages = new ArrayList<>();

            // Mengonversi gambar produk jika ada
            for (StoreProduct storeProduct : storeProducts) {
                try {
                    Product product = storeProduct.getProduct();
                    if (product != null && product.getImageProduct() != null) {
                        String base64Image = convertImage.convertImage(product.getImageProduct());
                        storeProduct.setBase64Image(base64Image); // Menyimpan gambar produk dalam format Base64
                    }
                    productsWithImages.add(storeProduct);
                } catch (Exception e) {
                    model.addAttribute("message", "Error processing product images");
                    e.printStackTrace(); // Menangani error saat mengonversi gambar produk
                }
            }

            // Menambahkan data toko dan produk ke dalam model
            model.addAttribute("store", userStore);
            model.addAttribute("products", productsWithImages);
            return "store/home-store"; // Mengembalikan halaman "home-store" untuk ditampilkan ke pengguna
        } else {
            return "redirect:/login"; // Jika pengguna tidak terautentikasi, arahkan ke halaman login
        }
    }

    ///////////////// PRODUCT /////////////////
    @GetMapping("/product/edit-product/{id}")
    public String updateProductPage(@PathVariable(value = "id") String id, Model model) {
        Product product = productRepository.getReferenceById(id);
        model.addAttribute("product", product);
        return "product/edit-product";
    }

    @GetMapping("/product/delete-product/{id}")
    public String deleteProduct(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Produk berhasil dihapus");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/stores/home-store";
    }

    ///////////////// TRANSACTION /////////////////
    @GetMapping("/buy/transaction/{id}")
    public String transactionPage(@PathVariable(value = "id") String id, Model model) {
        // Mendapatkan informasi autentikasi pengguna yang sedang login
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        // Mencari produk berdasarkan ID yang diberikan di URL
        Product product = productRepository.findProductById(id); // Menggunakan findById
    
        // Mengecek apakah pengguna terautentikasi
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findUserByUsername(username);
    
            // Memeriksa apakah pengguna ditemukan
            if (user == null) {
                model.addAttribute("message", "User not found");
                return "redirect:/login"; // Jika pengguna tidak ditemukan, arahkan ke halaman login
            }
    
            // Jika produk ditemukan, tambahkan produk dan pengguna ke model
            if (product != null) {
                System.out.println("Product found: " + product.getProductName());
                model.addAttribute("product", product); // Menambahkan produk ke model
                model.addAttribute("user", user); // Menambahkan pengguna ke model
            } else {
                // Jika produk tidak ditemukan, tampilkan pesan di console
                System.out.println("Product not found!");
            }
        }
        return "transaction/transaction"; // Mengembalikan halaman transaksi untuk ditampilkan ke pengguna
    }
    
    @GetMapping("/buy/history")
    public String userHistoryPage(@RequestParam(value = "sort", required = false) String sort, Model model) {
        // Mendapatkan informasi autentikasi pengguna yang sedang login
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
    
        // Mencari pengguna berdasarkan username
        User user = userRepository.findUserByUsername(username);
    
        // Mengambil semua transaksi yang dimiliki oleh pengguna
        List<Transaction> transactions = transactionRepository.findTransactionByUser(user);
    
        // Memfilter transaksi berdasarkan waktu jika parameter 'sort' disertakan dalam URL
        if (sort != null && !sort.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
    
            // Variabel final untuk menyimpan batas waktu (timestamp)
            final LocalDateTime timeTamp;
    
            // Menentukan batas waktu berdasarkan pilihan pengguna
            switch (sort) {
                case "1":
                    timeTamp = now.minusMinutes(1); // Transaksi dalam 1 menit terakhir
                    break;
                case "5":
                    timeTamp = now.minusMinutes(5); // Transaksi dalam 5 menit terakhir
                    break;
                case "10":
                    timeTamp = now.minusMinutes(10); // Transaksi dalam 10 menit terakhir
                    break;
                default:
                    timeTamp = null; // Tidak ada filter waktu jika tidak ada pilihan
                    break;
            }
    
            // Jika timeTamp tidak null, filter transaksi berdasarkan batas waktu
            if (timeTamp != null) {
                transactions = transactions.stream()
                        .filter(transaction -> transaction.getDateTransaction().isAfter(timeTamp)) // Menyaring transaksi yang lebih baru dari waktu yang ditentukan
                        .toList(); // Mengumpulkan hasil filter menjadi list
            }
        }
    
        // Menambahkan daftar transaksi ke dalam model untuk ditampilkan di halaman history
        model.addAttribute("transactions", transactions);
    
        // Mengembalikan tampilan halaman history dengan data transaksi yang sudah difilter
        return "history/history";
    }
    
    @GetMapping("/buy/delete-history/{id}")
    public String transactionPage(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            transactionService.deleteTransaction(id);
            redirectAttributes.addFlashAttribute("message", "Berhasil Di Hapus");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/buy/history";
    }
}
