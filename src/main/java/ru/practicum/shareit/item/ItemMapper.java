package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookerInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ItemMapper {
    public static ItemDto toDto(Item item, Map<String,BookerInfoDto> mapBooking, List<CommentDto> commentDtos) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .lastBooking(mapBooking.get("LastBooking"))
                .nextBooking(mapBooking.get("NextBooking"))
                .comments(commentDtos)
                .build();
        System.out.println("ItemMapper.toDto(3) =" + itemDto);
        return itemDto;
    }

    public  static ItemDto toDto(Item item, List<CommentDto> commentDtos) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .lastBooking(null)
                .nextBooking(null)
                .comments(commentDtos)
                .build();
        System.out.println("ItemMapper.toDto(2) =" + itemDto);
        return itemDto;
    }

    public  static ItemDto toDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
        System.out.println("ItemMapper.toDto(1) =" + itemDto);
        return itemDto;
    }

    public  static Item fromDto(ItemDto itemDto, UserDto userDto) {
        User user = User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
    }

    public  static List<ItemDto> toDtos(List<Item> items) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            result.add(toDto(item));
        }
        return result;
    }
}
