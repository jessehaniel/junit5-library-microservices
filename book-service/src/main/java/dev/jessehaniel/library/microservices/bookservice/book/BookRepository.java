package dev.jessehaniel.library.microservices.bookservice.book;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface BookRepository extends JpaRepository<Book, Integer> {

}

