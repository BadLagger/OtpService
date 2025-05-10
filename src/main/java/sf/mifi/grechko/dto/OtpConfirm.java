package sf.mifi.grechko.dto;

import lombok.Data;

import java.util.List;

@Data
public class OtpConfirm {
    private List<Integer> numbers;
}
