package by.tukai.flats.repos;

import by.tukai.flats.models.Flat;
import org.springframework.data.repository.CrudRepository;

public interface FlatRep extends CrudRepository<Flat, Long> {
    long deleteByAddress(String address);
    Flat findByAddress(String address);
}
