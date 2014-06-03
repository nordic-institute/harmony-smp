/*
 * 
 */
package eu.domibus.backend.db.dao;

import eu.domibus.backend.db.model.Payload;

import java.util.List;

/**
 * The Interface IPayloadDAO.
 */
public interface IPayloadDAO {

    /**
     * Save.
     *
     * @param entity the entity
     */
    public void save(Payload entity);

    /**
     * Delete.
     *
     * @param entity the entity
     */
    public void delete(Payload entity);

    /**
     * Update.
     *
     * @param entity the entity
     * @return the payload
     */
    public Payload update(Payload entity);

    /**
     * Find by id.
     *
     * @param id the id
     * @return the payload
     */
    public Payload findById(Integer id);

    /**
     * Find by property.
     *
     * @param propertyName        the property name
     * @param value               the value
     * @param rowStartIdxAndCount the row start idx and count
     * @return the list
     */
    public List<Payload> findByProperty(String propertyName, Object value, int... rowStartIdxAndCount);

    /**
     * Find by file name.
     *
     * @param fileName            the file name
     * @param rowStartIdxAndCount the row start idx and count
     * @return the list
     */
    public List<Payload> findByFileName(Object fileName, int... rowStartIdxAndCount);

    /**
     * Find all.
     *
     * @param rowStartIdxAndCount the row start idx and count
     * @return the list
     */
    public List<Payload> findAll(int... rowStartIdxAndCount);
}