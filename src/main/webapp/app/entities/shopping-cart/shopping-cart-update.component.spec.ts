import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import dayjs from 'dayjs';
import sinon, { type SinonStubbedInstance } from 'sinon';

import CustomerService from '@/entities/customer/customer.service';
import AlertService from '@/shared/alert/alert.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';

import ShoppingCartUpdate from './shopping-cart-update.vue';
import ShoppingCartService from './shopping-cart.service';

type ShoppingCartUpdateComponentType = InstanceType<typeof ShoppingCartUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shoppingCartSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ShoppingCartUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ShoppingCart Management Update Component', () => {
    let comp: ShoppingCartUpdateComponentType;
    let shoppingCartServiceStub: SinonStubbedInstance<ShoppingCartService>;

    beforeEach(() => {
      route = {};
      shoppingCartServiceStub = sinon.createStubInstance<ShoppingCartService>(ShoppingCartService);
      shoppingCartServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        toast: {
          show: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          shoppingCartService: () => shoppingCartServiceStub,
          customerService: () =>
            sinon.createStubInstance<CustomerService>(CustomerService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(ShoppingCartUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ShoppingCartUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shoppingCart = shoppingCartSample;
        shoppingCartServiceStub.update.resolves(shoppingCartSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shoppingCartServiceStub.update.calledWith(shoppingCartSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        shoppingCartServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ShoppingCartUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shoppingCart = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shoppingCartServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        shoppingCartServiceStub.find.resolves(shoppingCartSample);
        shoppingCartServiceStub.retrieve.resolves([shoppingCartSample]);

        // WHEN
        route = {
          params: {
            shoppingCartId: `${shoppingCartSample.id}`,
          },
        };
        const wrapper = shallowMount(ShoppingCartUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.shoppingCart).toMatchObject(shoppingCartSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shoppingCartServiceStub.find.resolves(shoppingCartSample);
        const wrapper = shallowMount(ShoppingCartUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
