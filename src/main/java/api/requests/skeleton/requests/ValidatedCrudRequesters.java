package api.requests.skeleton.requests;

import api.requests.skeleton.interfaces.GetAllEndpointInterface;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.BaseModel;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.HttpRequest;
import api.requests.skeleton.interfaces.CrudEndpointInterface;

import java.util.Arrays;
import java.util.List;

public class ValidatedCrudRequesters<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface, GetAllEndpointInterface {
    private CrudRequesters crudRequesters;

    public ValidatedCrudRequesters(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
        super(requestSpecification, responseSpecification, endpoint);
        this.crudRequesters = new CrudRequesters(requestSpecification, responseSpecification, endpoint);
    }

    @Override
    public T post(BaseModel model) {
        return (T) crudRequesters.post(model).extract().as(endpoint.getResponseModel());
    }

    public T put(BaseModel model) {
        return (T) crudRequesters.put(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T get(Long id) {
        return (T) crudRequesters.get(id).extract().as(endpoint.getResponseModel());
    }

    @Override
    public Object update(long id, BaseModel model) {
        return null;
    }

    @Override
    public Object delete(long id) {
        return null;
    }

    @Override
    public List<T> getAll(Class<?> clazz) {
        T[] array = (T[]) crudRequesters.getAll(clazz).extract().as(clazz);
        return Arrays.asList(array);
    }
}
