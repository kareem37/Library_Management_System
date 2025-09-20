package com.example.library.bootstrap;

import com.example.library.model.*;
import com.example.library.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LanguageRepository languageRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository copyRepository;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           LanguageRepository languageRepository,
                           CategoryRepository categoryRepository,
                           AuthorRepository authorRepository,
                           BookRepository bookRepository,
                           BookCopyRepository copyRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.languageRepository = languageRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.copyRepository = copyRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().name("ROLE_ADMIN").description("Admin").build());
            roleRepository.save(Role.builder().name("ROLE_LIBRARIAN").description("Librarian").build());
            roleRepository.save(Role.builder().name("ROLE_MEMBER").description("Member").build());
        }

        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@local")
                    .fullName("Administrator")
                    .password(passwordEncoder.encode("admin123"))
                    .enabled(true)
                    .build();
            admin = userRepository.save(admin);
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
        }

        if (languageRepository.count() == 0) {
            languageRepository.save(Language.builder().code("en").name("English").build());
            languageRepository.save(Language.builder().code("ar").name("Arabic").build());
        }
        if (categoryRepository.count() == 0) {
            categoryRepository.save(Category.builder().name("General").description("General books").build());
        }
        if (authorRepository.count() == 0) {
            authorRepository.save(Author.builder().name("Unknown Author").bio("Auto seed").build());
        }

        // sample book
        if (bookRepository.count() == 0) {
            Book b = Book.builder()
                    .title("Sample Book")
                    .isbn("ISBN-0001")
                    .summary("Sample seeded book")
                    .publisher("Local Publisher")
                    .language(languageRepository.findByCode("en").orElse(null))
                    .category(categoryRepository.findByNameIgnoreCase("General").orElse(null))
                    .build();
            Author a = authorRepository.findByNameContainingIgnoreCase("Unknown").get(0);
            b.setAuthors(Set.of(a));
            Book saved = bookRepository.save(b);
            for (int i = 1; i <= 3; i++) {
                BookCopy c = BookCopy.builder()
                        .book(saved)
                        .copyNumber(i)
                        .barcode(String.format("BOOK-%d-COPY-%d", saved.getId(), i))
                        .status(com.example.library.model.CopyStatus.AVAILABLE)
                        .build();
                copyRepository.save(c);
            }
            saved.setTotalCopies(copyRepository.findByBookId(saved.getId()).size());
            bookRepository.save(saved);
        }
    }
}
