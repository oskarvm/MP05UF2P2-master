package ex3;

// Original source code: https://gist.github.com/amadamala/3cdd53cb5a6b1c1df540981ab0245479
// Modified by Fernando Porrino Serrano for academic purposes.

import java.util.ArrayList;

/**
 * Implementació d'una taula de hash sense col·lisions.
 * Original source code: https://gist.github.com/amadamala/3cdd53cb5a6b1c1df540981ab0245479
 */

public class HashTable {

    public class MiExcepcion extends Exception{
        String error;
        public MiExcepcion(String error) {
            super(error);
            this.setError(error);
        }

        public void setError(String error) {
            this.error = error;
        }
    }
    private int SIZE = 16;
    private int ITEMS = 0;
    private HashEntry[] entries = new HashEntry[SIZE];

    //Error 1: El count no añadia ITEMS++ en put y restaba ITEMS-- en drop
    public int count(){
        return this.ITEMS;
    }

    public int size(){
        return this.SIZE;
    }

    /**
     * Permet afegir un nou element a la taula.
     * @param key La clau de l'element a afegir.
     * @param value El propi element que es vol afegir.
     */
    // Error 2: Crear una entrada con una key ya existente, NO arreglado
    // Error 3: Cuando la key da un hash negativo no puede entrar en la Table y rompe
    public void put(Object key, Object value) {
        int hash = getHash(key);
        final HashEntry hashEntry = new HashEntry(key, value);


        if(hash < 0){
            MiExcepcion miExcepcion = new MiExcepcion("El hash de esta key es negativa");
            System.out.println(miExcepcion);
        }
        else {
            if (entries[hash] == null) {
                entries[hash] = hashEntry;
            } else {
                HashEntry temp = entries[hash];
                while (temp.next != null)
                    temp = temp.next;

                temp.next = hashEntry;
                hashEntry.prev = temp;
            }
            ITEMS++;//Añado un ITEMS con ITEMS++ para que sume uno en el count() al crear una entrada nueva.
        }
    }

    /**
     * Permet recuperar un element dins la taula.
     * @param key La clau de l'element a trobar.
     * @return El propi element que es busca (null si no s'ha trobat).
     */
    //Error 4: Recuperar un elemento de un entrada inexsistente da null
    public Object get(Object key) {
        int hash = getHash(key);
        boolean existe = true;

        if(entries[hash] != null) {
            HashEntry temp = entries[hash];

            while (!temp.key.equals(key)) {
                if (temp.next == null) {
                    existe = false;
                    break;
                }
                temp = temp.next;
            }

                if (existe) {
                    return temp.value;
                }
            }
        MiExcepcion miExcepcion = new MiExcepcion("La key del valor buscado no existe");
        System.out.println(miExcepcion);
        return null;
    }

    /**
     * Permet esborrar un element dins de la taula.
     * @param key La clau de l'element a trobar.
     */
    //Error 5: Borrar la primera entrada con colisiones
    //Error 6: Borrar una entrada que con un key que no existe, NO arreglado
    public void drop(Object key) {
        int hash = getHash(key);
        if(entries[hash] != null) {

            HashEntry temp = entries[hash];
            while( !temp.key.equals(key))
                temp = temp.next;

            if(temp.prev == null && temp.next == null){ entries[hash] = null; //Borrar entrada que no colosiona

            }
            else if(temp.prev != null){
                if(temp.next != null) temp.next.prev = temp.prev;   //Borrar entrada que este en medio o al final con colisiones
                temp.prev.next = temp.next;
            }
            else if(temp.prev == null && temp.next != null){ //Borrar la primera entrada el primero con colisiones
                temp.next.prev = null;
                HashEntry borrar = temp.next;
                entries[hash] = borrar;
            }
        }
        ITEMS--; //Quito un ITEMS con ITEMS-- para que reste uno en el count() al borrar una entrada nueva.
    }

    private int getHash(Object key) {
        // piggy backing on java string
        // hashcode implementation.
        return key.hashCode() % SIZE;
    }

    private class HashEntry {
        Object key;
        Object value;

        // Linked list of same hash entries.
        HashEntry next;
        HashEntry prev;

        public HashEntry(Object key, Object value) {
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
        }

        @Override
        public String toString() {
            return "[" + key + ", " + value + "]";
        }
    }

    @Override
    public String toString() {
        int bucket = 0;
        StringBuilder hashTableStr = new StringBuilder();
        for (HashEntry entry : entries) {
            if(entry == null) {
                bucket++;
                continue;
            }

            hashTableStr.append("\n bucket[")
                    .append(bucket)
                    .append("] = ")
                    .append(entry.toString());
            bucket++;
            HashEntry temp = entry.next;
            while(temp != null) {
                hashTableStr.append(" -> ");
                hashTableStr.append(temp.toString());
                temp = temp.next;
            }
        }
        return hashTableStr.toString();
    }

    /**
     * Permet calcular quants elements col·lisionen (produeixen la mateixa posició dins la taula de hash) per a la clau donada.
     * @param key La clau que es farà servir per calcular col·lisions.
     * @return Una clau que, de fer-se servir, provoca col·lisió amb la que s'ha donat.
     */
    public Object getCollisionsForKey(Object key) {
        return getCollisionsForKey(key, 1).get(0);
    }

    /**
     * Permet calcular quants elements col·lisionen (produeixen la mateixa posició dins la taula de hash) per a la clau donada.
     * @param key La clau que es farà servir per calcular col·lisions.
     * @param quantity La quantitat de col·lisions a calcular.
     * @return Un llistat de claus que, de fer-se servir, provoquen col·lisió.
     */
    public ArrayList<String> getCollisionsForKey(Object key, int quantity){
        /*
          Main idea:
          alphabet = {0, 1, 2}

          Step 1: "000"
          Step 2: "001"
          Step 3: "002"
          Step 4: "010"
          Step 5: "011"
           ...
          Step N: "222"

          All those keys will be hashed and checking if collides with the given one.
        * */

        final char[] alphabet = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        ArrayList<Integer> newKey = new ArrayList();
        ArrayList<String> foundKeys = new ArrayList();

        newKey.add(0);
        int collision = getHash(key);
        int current = newKey.size() -1;

        while (foundKeys.size() < quantity){
            //building current key
            String currentKey = "";
            for(int i = 0; i < newKey.size(); i++)
                currentKey += alphabet[newKey.get(i)];

            if(!currentKey.equals(key) && getHash(currentKey) == collision)
                foundKeys.add(currentKey);

            //increasing the current alphabet key
            newKey.set(current, newKey.get(current)+1);

            //overflow over the alphabet on current!
            if(newKey.get(current) == alphabet.length){
                int previous = current;
                do{
                    //increasing the previous to current alphabet key
                    previous--;
                    if(previous >= 0)  newKey.set(previous, newKey.get(previous) + 1);
                }
                while (previous >= 0 && newKey.get(previous) == alphabet.length);

                //cleaning
                for(int i = previous + 1; i < newKey.size(); i++)
                    newKey.set(i, 0);

                //increasing size on underflow over the key size
                if(previous < 0) newKey.add(0);

                current = newKey.size() -1;
            }
        }

        return  foundKeys;
    }

    public static void log(String msg) {
        System.out.println(msg);
    }
}