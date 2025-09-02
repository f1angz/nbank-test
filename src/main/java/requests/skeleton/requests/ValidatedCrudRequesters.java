package requests.skeleton.requests;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import requests.skeleton.Endpoint;
import requests.skeleton.HttpRequest;
import requests.skeleton.interfaces.CrudEndpointInterface;

public class ValidatedCrudRequesters<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface {
    private CrudRequesters crudRequesters;

    public ValidatedCrudRequesters(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
        super(requestSpecification, responseSpecification, endpoint);
        this.crudRequesters = new CrudRequesters(requestSpecification, responseSpecification, endpoint);
    }

    @Override
    public T post(BaseModel model) {
        return (T) crudRequesters.post(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public Object get(Long id) {
        return null;
    }

    @Override
    public Object update(long id, BaseModel model) {
        return null;
    }

    @Override
    public Object delete(long id) {
        return null;
    }
}
