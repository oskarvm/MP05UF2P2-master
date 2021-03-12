package ex1;

import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {


    @org.junit.jupiter.api.Test
    void put() {
        HashTable ht = new HashTable();
        ht.put("put1","1put");
        ht.put("put2","2put");
        ht.put("2","3put");
        ht.put("02","4put");
        ht.put("put1","5put");
        ht.put("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa","6put"); //java.lang.ArrayIndexOutOfBoundsException: Index -15 out of bounds for length 16

        ht.count();
        ht.size();
        ht.toString();
        Assertions.assertEquals(5,ht.count());
        Assertions.assertEquals(16,ht.size());
        Assertions.assertEquals("\n bucket[2] = [put1, 1put] -> [2, 3put] -> [02, 4put]\n bucket[3] = [put2, 2put]",ht.toString());
    }

    @org.junit.jupiter.api.Test
    void get() {

        HashTable ht = new HashTable();
        ht.put("put1","1put");
        ht.put("put2","2put");
        ht.put("2","3put");
        ht.put("02","4put");

        String sinColision = ht.get("put2");
        String conColisionInicio = ht.get("put1");
        String conColisionCentro = ht.get("2");
        String conColisionFinal = ht.get("02");
        String noExiste = ht.get("put3");

        Assertions.assertEquals("2put",sinColision);
        Assertions.assertEquals("1put", conColisionInicio);
        Assertions.assertEquals("3put", conColisionCentro);
        Assertions.assertEquals("4put", conColisionFinal);
        Assertions.assertEquals(null, noExiste);
    }

    @org.junit.jupiter.api.Test
    void drop() {
        HashTable ht = new HashTable();

        //Crear 4 lineas

        ht.put("put1","1put");
        ht.put("put2","2put");
        ht.put("2","3put");
        ht.put("02","4put");

        ht.drop("put1");
        ht.drop("02");
        ht.drop("put2");
        ht.drop("2");


        ht.count();
        ht.size();
        ht.toString();
        Assertions.assertEquals(0,ht.count());
        Assertions.assertEquals(16,ht.size());
        Assertions.assertEquals("\n bucket[2] = [put1, 1put] -> [2, 3put] -> [2, 4put]\n bucket[3] = [put2, 2put]",ht.toString());
    }
}