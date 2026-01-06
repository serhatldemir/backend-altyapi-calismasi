package com.staj.backend_gorev.mapper;

import com.staj.backend_gorev.dto.UserDTO;
import com.staj.backend_gorev.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity -> DTO
    // veri güvenligi icin password örnegini hatirla
    UserDTO toDTO(User user);
    //@mapping dto ile entity degerlerini karsılastirir.Aynı ise mapping yazmasanda olur ama biz hep target = id ... kullanmaliyiz.
    // DTO -> Entity (Yeni Kayıt İçin)
    // ID'yi dışarıdan gelen veriden almıyoruz, veritabanı kendi üretecek.
    @Mapping(target = "id", ignore = true) 
    User toEntity(UserDTO userDTO);

    //  Update İşlemi
    // Var olan bir Entity nesnesini (user), DTO'daki verilerle günceller.
    // Tek tek set/get yapmaktan kurtarır. ID değişmemeli, o yüzden ignore ediyoruz.
    @Mapping(target = "id", ignore = true)
    void updateUserFromDTO(UserDTO userDTO, @MappingTarget User user);
    //MappingTargetle yeni nesne yaratmaz direkt depodaki nesneyi günceller.Var olan nesnede islem yapar .
}