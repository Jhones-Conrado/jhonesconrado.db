# jhonesconrado.db
Simple and lightweight Java BD for small applications that saves data to storage.

##English
A simple database that will save entities in subfolders, forming a Database.
The project already contains methods to save, load by ID, load by filter, load all, delete by ID, delete by filter, delete all.

A folder will be created whose name is the same as the name of the class that will be saved. Saved objects will automatically receive a unique ID before being saved.

Entities will be saved in JSON format, so if any changes are made to the entity model, it will not affect the loading of entities that have already been saved.

To have access to all DB methods, such as save and load, the entity must implement the Entity interface.

The entity must have a **long** field with a value of -1l to store the ID.
Implement a **getId** method to return the created variable.
And implement a **onSetId** method to put the new ID in the variable, before saving it.

In case of doubt, a previously configured entity already exists in the project, called PreEntity. Your entities can extend from this.

##Português
Um simples banco de dados que irá salvar as entidades em subpastas, formando um Banco de Dados.
O projeto já contém métodos para salvar, carregar por ID, carregar por filtro, carregar todos, deletar por ID, deletar por filtro, deletar todos.

Será criado uma pasta cujo nome é igual ao nome da classe que será salva. Os objetos salvos receberão automaticamente um ID único antes de ser salvos.

As entidades serão salvas no formato JSON, desta forma, caso alguma auteração seja feita no modelo da entidade, isso não afetará o carregamento das entidades que já foram salvas.

Para ter acesso à todos os métodos do BD, como salvar e carregar, a entidade deve implementar a interface Entity.

A entidade deverá contar um campo **long** com valor igual a -1l para guardar o ID.
Implementar um método **getId** para retornar a variável criada.
E implementar um método **onSetId** para por o novo ID na variável, antes de salvá-la.

Em caso de dúvidas, uma entidade previamente configurada já existe no projeto, chamada PreEntity. Suas entidades podem extender desta.

##Example of implementation / Exemplo de implementação
```java
public class PreEntity implements Entity{
    
    private long id = -1l;
    
    /**
     * @return The ID number of this entity.
     */
    @Override
    public long getId() {
        return id;
    }

    /**
     * @param id Set a new ID for this entity.
     */
    @Override
    public void onSetId(long id) {
        this.id = id;
    }
    
}
```

##How to create and save a entity / Como criar e salvar uma entidade.
Using the entity above as an example.

```java
        //saving an entity
        //Salvando uma entidade
        PreEntity entity = new PreEntity();
        entity.save();
        
        //Loading an entity from DB
        //Carregando uma entidade do BD
        PreEntity loadedFromDBEntity = new PreEntity().load(ID);
        
        //Creating a filter and reading the entities through the filter
        //Criando um filtro e lendo entidades pelo filtro.
        Map<String, String> filter = new HashMap<>();
        filter.put("name", "Jhones Conrado");
        List<Entity> loadedFromDBWithFilter = new PreEntity().loadAll(filter, true, true);
        
        //Filters entities by two different cases. They must have the name and date field informed.
        //Filtra as entidades por dois casos diferentes. Precisam ter o campo nome e data informados.
        Map<String, String> otherFilter = new HashMap<>();
        otherFilter.put("name", "Jhones Conrado");
        otherFilter.put("date", "10-03-2022");
        List<Entity> loadedFromDBWithMultiFilter = new PreEntity().loadAll(filter, true, true);
        
        //Deleting an entity.
        //Deletando uma entidade.
        new PreEntity().load(ID).delete();
        
        //Deleting entities by filter.
        //Deletando entidades por filtro.
        Map<String, String> filterToDelete = new HashMap<>();
        filterToDelete.put("name", "Jhones Conrado");
        new PreEntity().deleteAllByFilter(filterToDelete, true, true);
        
        //Delete all entites.
        //Deletar todas as entidades.
        new PreEntity().deleteAll();
```

##Using filter / Usando filtro
###English
You can create a Map<String, String> and put the "fields" x "values" you want to filter.

###The loadAll(filter, boolean, boolean) method
The first boolean is for whether you want the word "value" in the filter to match fully or partially. True to fully match, false to partially match.

The second boolean is used to know if all fields need to return true in the verification, or if just one returning true is enough.

###Português
Você pode criar um Map<String, String> e por os "campos" x "valores" que deseja filtrar.

###O método loadAll(filter, boolean, boolean)
O primeiro boleano serve para saber se você quer que a palavra "valor" do filtro precise coincidir inteiramente ou parcialmente. Verdadeiro para coincidir inteiramente, falso para coincidir parcialmente.

O segundo boleano serve para saber se todos os campos precisam retornar verdadeiro na verificação, ou se apenas um retornando verdadeiro já basta.

