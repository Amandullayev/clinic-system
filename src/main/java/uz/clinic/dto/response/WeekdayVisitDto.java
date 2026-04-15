package uz.clinic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeekdayVisitDto {
    private String day;
    private long count;
}