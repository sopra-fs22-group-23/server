package ch.uzh.ifi.sopra22.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

    private FileService fileService;

    @Test
    public void testRandomString_successfulConversion(){
        fileService = new FileService();
        String beginPath = "test.png";
        String actualString = fileService.createNameWithTimestampAndID(beginPath,1L);

        assertNotEquals(beginPath,actualString);
        assertEquals(beginPath.substring(beginPath.lastIndexOf(".") + 1), actualString.substring(actualString.lastIndexOf(".") + 1));
    }

}