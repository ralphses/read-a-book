package online.contactraphael.readabook.model.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.contactraphael.readabook.model.user.AppUser;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(generator = "book_id_generator", strategy = SEQUENCE)
    @SequenceGenerator(name = "book_id_generator", sequenceName = "book_id_generator", allocationSize = 1)
    private Long id;
    private String bookCode;

    private Integer noOfPages;
    private Double price;

    private String licence;
    private String yearPublished;
    private String author;
    private String title;
    private String summary;
    private String edition;
    private String url;

    @Enumerated(EnumType.STRING)
    private BookType bookType;

    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private BookUploader uploadedBy;



}

