package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Xiaobo Bi (869384236@qq.com)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PltCountriesAvailable {
    private String country;
    private boolean ib;
    private boolean ob;
}
