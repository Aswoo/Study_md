### Hilt 란?

Hilt는 기존 Dagger2를 보일러 플레이트 코드를 제거하기 위해 안드로이드의 구조에 맞춰 몇 가지 Annotaion을 추가한 라이브러리이다.
안드로이드 11 에서  공식적으로 Jetpack에 합류했다.



### 1. 시작하기

```
/-- build.gradel(project) --/

classpath 'com.google.dagger:hilt-android-gradle-plugin:2.28-alpha'  

-------------------------------------------------------------------------------

/-- build.gradel(app) --/

apply plugin: 'dagger.hilt.android.plugin'

dependencies {  
  implementation "com.google.dagger:hilt-android:2.28-alpha"  
  kapt "com.google.dagger:hilt-android-compiler:2.28-alpha"

  //viewModel 관련된 확장 라이브러리  
  implementation 'androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha01'  
  kapt 'androidx.hilt:hilt-compiler:1.0.0-alpha01'  
}
```

현재는 Alpha 버전으로 추후 변경될 여지가 있다.

### 2. Application 설정

```
@HiltAndroidApp  
class ToDoApplication : Application()
```

`@HiltAndroidApp`로 가능

 

Dagger2는 `DaggerApplication` 를 상속받아서 Application을 만들고 모듈을 제공하기 위해 `AndroidInjector<DaggerApplication>` 도 상속받아 구현해야 했다.

 

Hilt에서는 `@InstallIn(ApplicationComponent::class)` 코드를 Module에 붙이면 앱에서 해당 모듈을 제공할 수 있다.

context, application을 제공하기 위한 ApplicationContextModule 또한 미리 정의되어 있다.

### 3. Activity, Fragment 그리고 뷰 모델

```
@AndroidEntryPoint  
class ToDoActivity : AppCompatActivity() {

  private val toDoViewModel: ToDoViewModel by viewModels ()

  ...
}
```

`@AndroidEntryPoint` 로 가능

 

Dagger2에서 `DaggerActivity`, `DaggerFragment` 등을 상속받는 코드를 간단한 Annotation으로 대체하였다.

 

현재는 `Activity`, `Fragment`, `View`, `Service,` `BroadcastReceiver` 에 대해서만 지원한다.
그 외에 클래스에 대한 주입은 `@AndroidEntryPoint` 을 통해서 할 수 없다. ( 사실 저거면 충분한 거 같다.. )

```
class ToDoViewModel @ViewModelInject constructor(  
  private val toDoRepository: ToDoRepository,  
  @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {

  ...

}
```

`@ViewModelInject` 로 가능 , 확장 라이브러리에 포함되어 있다.

 

Dagger2 에서는 ViewModelStoreOwner를 지정해주기 위해서 ViewModel 이 아닌 ViewModelFactory에 주입을 하여 사용했다.

 

Hilt의 `@ViewModelInject` 를 사용하면 기존 방법대로 뷰모델을 생성만 해도 주입된 뷰 모델을 가져올 수 있수 있도록ViewModelFactoryModules 로 정의 해놓았다.

 

또한 `@Assisted` annotation을 붙이면 `SavedStateHandle` 를 주입해준다.

### 4. Module

```
@Module  
@InstallIn(ApplicationComponent::class)  
class RoomModule {

  @Provides  
  @Singleton  
  fun provideAppDatabase(@ApplicationContext context: Context): ToDoDatabase {  
    return Room.databaseBuilder(context,  
            ToDoDatabase::class.java, "todo_database")  
            .fallbackToDestructiveMigration()  
            .allowMainThreadQueries()  
            .build()  
  }

  @Provides  
  @Singleton  
  fun provide(appDatabase: ToDoDatabase): ToDoDao {  
      return appDatabase.toDoDao()  
  }

}

@Module  
@InstallIn(ApplicationComponent::class)  
class RepositoryModule {

  @Binds  
  @Singleton  
  fun getToDoRepository(toDoRepo: ToDoRepositoryImpl): ToDoRepository

}

@Singleton  
class ToDoRepositoryImpl @Inject constructor(private val toDoDao: ToDoDao) : ToDoRepository {  

  ... 

}
```

모듈은 기존 Dagger2와 동일하게 작성한 후 `@InstallIn(ApplicationComponent::class)` 를 통해 `@Component` 를 따로 만들지 않고도 컴포넌트에 추가할 수 있다.

 

ApplicationComponent 외에도 ActivityRetainedComponent, ActivityComponent, FragmentComponent, ViewComponent , ViewWithFragmentComponent, ServiceComponent을 Hilt에서 미리 정의해 놓았다.

 

ApplicationContextModule에서 제공하는 context를 주입받으려면 `@ApplicationContext` 를 사용하면 된다.

### 5. 마무리

Dagger2에서 작성해야 하는 보일러 플레이트 코드 들이 Hilt에서 확실하게 줄어든 느낌이다.
위에 나온 몇 개의 annotation 정도면 기본적인 앱을 만들기엔 충분해 보인다.