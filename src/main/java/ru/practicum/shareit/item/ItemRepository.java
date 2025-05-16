package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerIdOrderByIdAsc(long userId);

    Optional<Item> findByOwnerIdAndId(long userId,long itemId);

    @Query("select i from Item i " +
            "where (lower(i.name) like lower(concat('%',:text,'%')) " +
            "or lower(i.description) like lower(concat('%',:text,'%'))) and i.available = true")
    List<Item> getBySearch(@Param("text") String text);
}
